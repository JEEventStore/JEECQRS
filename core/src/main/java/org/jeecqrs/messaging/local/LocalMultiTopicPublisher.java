/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.messaging.local;

import org.jeecqrs.messaging.MultiTopicPublisher;
import org.jeecqrs.messaging.MultiTopicSubscriber;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

/**
 * An in-memory, transaction aware publisher with at-least-once delivery
 * and global message order guarantees.
 * <p>
 * The global order of messages per subsequent call of {@link #publish} 
 * within a single thread is guaranteed, but the order of messages of
 * parallel calls to {@link publish} from multiple threads is unexpected, even
 * if they arrive in the same topic. 
 * <p>
 * When a subscriber is failing, the delivery is retried a finite number of
 * times (configurable).  When the maximum number of retries has been
 * exhausted, the subscriber is detached from the subscription.
 * <p>
 * The maximum number of delivery attempts can be configured with the
 * {@code deliveryAttempts} environment entry. The default value is 10 attempts.
 * <p>
 * The retry interval for failed deliveries can be configured with the
 * {@code retryInterval} environment entry. The default value is 10 ms.
 * 
 * @param <M>  the message type
 */
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class LocalMultiTopicPublisher<M> implements MultiTopicPublisher<M> {
    
    private static final Logger log = Logger.getLogger(LocalMultiTopicPublisher.class.getCanonicalName());

    @Resource
    private TimerService timerService;

    @Resource(name = "deliveryAttempts")
    private long deliveryAttempts = 10;

    @Resource(name = "retryInterval")
    private long retryInterval = 10;

    private final Map<String, Set<MultiTopicDelivery>> subscriptionsByTopic;
    private final java.util.concurrent.locks.Lock subscriptionsByTopicLock = new ReentrantLock();

    private final Map<MultiTopicSubscriber, MultiTopicDelivery> subscriptionsBySubscriber;
    private final java.util.concurrent.locks.Lock subscriptionsBySubscriberLock = new ReentrantLock();


    public LocalMultiTopicPublisher() {
        this.subscriptionsByTopic = new ConcurrentHashMap<>();
        this.subscriptionsBySubscriber = new ConcurrentHashMap<>();
    }

    /**
     * Publishes the given {@code message} in the given {@code topic}.
     * Concurrent calls of this function are permitted.  See the
     * class documentation for message order guarantees.
     * 
     * @param topic   the topic the message is published in
     * @param message the message to be published
     */
    @Override
    @Lock(LockType.READ)
    public void publish(String topic, M message) {
        log.log(Level.FINER, "Publishing new message in topic {0}: {1}",
                new Object[]{topic, message});
        Collection<MultiTopicDelivery> subs = subscriptionsByTopic.get(topic);
        if (subs == null || subs.isEmpty()) {
            log.log(Level.FINER,
                    "Message without subscriber: {0} @ {1}",
                    new Object[]{message.getClass(), topic});
            return;
        }
        // append the message to the delivery queue for each interested party
        for (MultiTopicDelivery s : subs)
            s.scheduleForDelivery(topic, message);
        // now schedule asynchronous delivery in a new thread
        this.scheduleDelivery(0, topic);
    }

    /**
     * Schedules an asynchronous delivery of messages in the given topic.
     * 
     * @param timeout  timeout after which the delivery is started
     * @param topic    the topic for which delivery is scheduled
     */
    private void scheduleDelivery(long timeout, String topic) {
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        config.setInfo(topic);
        timerService.createSingleActionTimer(timeout, config);
    }

    /**
     * Starts a delivery run.
     * This method is called asynchronously from the EJB TimerService.
     * <p>
     * Note that no transaction is active when this method is called
     * ({@code TransactionAttributeType.NEVER}), since we want to
     * gracefully handle errors of the subscribers.
     * <p>
     * Suppose there was an active transaction A when this method is called,
     * and suppose the subscriber is an EJB session bean.  Suppose furthermore
     * that the session bean raises a runtime exception during the message
     * handling in {@link MultiTopicSubscriber#receiveMessage}.  This causes
     * an {@link EJBException}, which immediately forces the surrounding
     * transaction A into roll-back-only state, even if we manually catch the
     * {@link EJBException}.  
     * (see, e.g., http://stackoverflow.com/questions/8490684/ejb-avoid-transaction-rollback )
     * We would therefore be forced to rollback the
     * transaction(s) for <b>all</b> subscribers (possibly requiring
     * two-phase-commits in those subscribers), or leave the subscribers and the
     * delivery queue in an undefined state.  To avoid this situation, we
     * require that no transaction is active in {@link #deliver}.  Now, if 
     * some subscriber throws a RuntimeException, the exception is caught and
     * gracefully handled by scheduling a new delivery attempt;  also, other
     * subscribers are not affected.
     * <p>
     * Subscribers may either be transaction-less as well (say, for example,
     * a view projection with an in-memory data store), or support transaction
     * creation by the usual JEE means (e.g. {@code TransactionAttributeType.REQUIRED}).
     * <p>
     * @see MultiTopicPublisherImplTest#test_delivery_transaction_independence
     * @param timer  the EJB timer object
     */
    @Timeout
    @Lock(LockType.READ) // allow parallel threads
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void deliver(Timer timer) {
        String topic = (String) timer.getInfo();
        log.log(Level.FINER,
                "Delivery started for topic ''{0}'' in thread #{1}",
                new Object[]{topic, Thread.currentThread().getId()});
        Collection<MultiTopicDelivery> subs = subscriptionsByTopic.get(topic);
        boolean pending = false;
        for (MultiTopicDelivery s : subs) {
            s.deliver();
            if (s.hasPending())
                pending = true;
        }
        if (pending) {
            log.log(Level.FINE, "Delivery finished, but pending jobs, reschedule delivery.");
            scheduleDelivery(retryInterval, topic);
        } else {
            log.log(Level.FINE, "All messages delivered.");
        }
    }

    /**
     * Notifies the publisher that one of the subscribers has failed to receive the message.
     * Cancels the subscription and notifies the subscriber.
     * 
     * @param subscriber  the subscriber that has failed
     */
    protected void subscriberFailing(MultiTopicSubscriber<M> subscriber) {
        log.severe("Subscriber failing, cancel subscription: " + subscriber);
        this.unsubscribe(subscriber);
        subscriber.canceledSubscription();
    }

    @Override
    @Lock(LockType.READ)
    public void subscribe(MultiTopicSubscriber<M> subscriber) {
        MultiTopicDelivery delivery = deliveryFor(subscriber);
        for (String topic: subscriber.interestedInTopics()) {
            Set<MultiTopicDelivery> dbt = deliveriesFor(topic);
            dbt.add(delivery);
        }
    }

    @Override
    @Lock(LockType.READ)
    public void unsubscribe(MultiTopicSubscriber<M> subscriber) {
        MultiTopicDelivery<M> delivery = deliveryFor(subscriber);
        for (String topic: subscriber.interestedInTopics()) {
            Set<MultiTopicDelivery> dbt = deliveriesFor(topic);
            dbt.remove(delivery);
        }
        subscriptionsBySubscriber.remove(subscriber);
    }

    /**
     * Gets or creates delivery object for the given subscriber.
     * 
     * @param subscriber  the subscriber
     * @return  the new or existing delivery object for the given subscriber
     */
    private MultiTopicDelivery deliveryFor(final MultiTopicSubscriber<M> subscriber) {
        MultiTopicDelivery delivery = null;
        subscriptionsBySubscriberLock.lock(); // atomic test-and-set for the subscriber
        try {
            delivery = subscriptionsBySubscriber.get(subscriber);
            if (delivery == null) {
                delivery = new MultiTopicDelivery(subscriber);
                delivery.setMaxAttempts(deliveryAttempts);
                delivery.setSubscriberFailingCallbck(new SubscriberFailingCallback() {
                    @Override
                    public void isFailing() {
                        LocalMultiTopicPublisher.this.subscriberFailing(subscriber);
                    }
                });
                subscriptionsBySubscriber.put(subscriber, delivery);
            }
        } finally {
            subscriptionsBySubscriberLock.unlock();
        }
        return delivery;
    }

    /**
     * Gets or creates the set of all delivery objects for a given topic.
     * 
     * @param topic  the topic
     * @return  all deliveries listening on the given topic
     */
    private Set<MultiTopicDelivery> deliveriesFor(String topic) {
        Set<MultiTopicDelivery> set = null;
        subscriptionsByTopicLock.lock(); // atomic test-and-set
        try {
            set = subscriptionsByTopic.get(topic);
            if (set == null) {
                set = Collections.newSetFromMap(new ConcurrentHashMap<MultiTopicDelivery, Boolean>());
                subscriptionsByTopic.put(topic, set);
            }
        } finally {
            subscriptionsByTopicLock.unlock();
        }
        return set;
    }
    
}
