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

package org.jeecqrs.event.bus.local;

import org.jeecqrs.messaging.MultiTopicPublisher;
import org.jeecqrs.messaging.MultiTopicSubscriber;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.event.EventBus;
import org.jeecqrs.event.EventInterest;
import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventBusListenerRegistry;

/**
 *
 */
public class LocalEventBus<E> implements EventBus<E> {

    private static final Logger log = Logger.getLogger(LocalEventBus.class.getCanonicalName());

    @EJB(name="multiTopicPublisher")
    private MultiTopicPublisher mtp;

    @EJB(name="listenerRegistry")
    private EventBusListenerRegistry<E> listenerRegistry;

    @Resource(name="topicGenerationStrategy")
    private Class<? extends TopicGenerationStrategy> topicGeneratorClass = DefaultTopicGenerationStrategy.class;
    private TopicGenerationStrategy<E> topicGenerator;

    private Map<EventBusListener, MultiTopicSubscriber<E>> map = new HashMap<>();

    @PostConstruct
    public void init() {
        if (topicGeneratorClass == null)
            throw new IllegalArgumentException("topicGenerationStrategy must not be null");
        try {
            this.topicGenerator = topicGeneratorClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("Cannot create topic generator instance: " + e.getMessage(), e);
        }
        subscribeListeners();
    }

    @Override
    @Lock(LockType.READ)
    public void dispatch(E event) {
	log.log(Level.FINER, "Dispatching event: {0}", event);
        Class<? extends E> eventClass = (Class<? extends E>) event.getClass();
        mtp.publish(topicGenerator.topicFor(eventClass), event);
        mtp.publish(topicGenerator.wildcardTopic(), event);
    }

    protected void subscribeListeners() {
        Set<EventBusListener<E>> listeners = listenerRegistry.allListeners();
        for (EventBusListener<E> l : listeners)
            subscribe(l);
    }

    protected void subscribe(EventBusListener<E> listener) {
        MultiTopicSubscriber mts = subscriberFor(listener);
        if (mts != null)
            throw new IllegalStateException("Subscriber already subscribed: " + listener);
        mts = createSubscriberFor(listener);
        mtp.subscribe(mts);
        map.put(listener, mts);
	log.log(Level.INFO, "Now serving {0} listeners.", new Object[]{map.size()});
    }

    protected void unsubscribe(EventBusListener<E> listener) {
        MultiTopicSubscriber mts = subscriberFor(listener);
        if (mts == null)
            throw new IllegalStateException("No such subscriber registered: " + listener);
        map.remove(listener);
        mtp.unsubscribe(mts);
    }

    private MultiTopicSubscriber<E> subscriberFor(EventBusListener<E> listener) {
        return map.get(listener);
    }

    protected MultiTopicSubscriber<E> createSubscriberFor(final EventBusListener<E> listener) {
        return new MultiTopicSubscriber<E>() {
            @Override
            public void receiveMessage(String topic, E message) {
                listener.receiveEvent(message);
            }

            @Override
            public void canceledSubscription() {
                log.severe("Error: Forced unsubscription of EventBusListener " +
                        listener + " / " + listener.getClass());
                map.remove(listener);
            }

            @Override
            public Set<String> interestedInTopics() {
                EventInterest<E> interest = listener.interestedInEvents();
                Set<String> result = new HashSet<>();
                for (Class<? extends E> c : interest.interestEventTypes())
                    result.add(topicGenerator.topicFor(c));
                return result;
            }
        };
    }

}
