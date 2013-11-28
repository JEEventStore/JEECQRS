package org.jeecqrs.messaging.local;

import org.jeecqrs.messaging.MultiTopicSubscriber;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Delivers messages to a subscriber with graceful error handling
 * while assuring global message order relative to the subscriber.
 * 
 * @param <M>  the message type
 */
public class MultiTopicDelivery<M> {

    private static final Logger log = Logger.getLogger(MultiTopicDelivery.class.getCanonicalName());

    private final MultiTopicSubscriber<M> subscriber;  // the subscriber that is supposed to receive the messages
    private SubscriberFailingCallback failingSubCB;    // callback for failing subscribers
    private long maxAttempts = 10;                     // max # of attepmts to deliver a message
    private long failedAttempts = 0;                   // # of failed attempts to deliver a message
    private final Queue<MessageEnvelope<M>> messageQueue; // queue of pending messages
    private final Lock lock;                           // lock to restrict deliver()

    public MultiTopicDelivery(MultiTopicSubscriber subscriber) {
        this.messageQueue = new ConcurrentLinkedQueue<>();
        this.lock = new ReentrantLock();
        this.subscriber = subscriber;
    }

    /**
     * Schedules a message for delivery.
     * This method is thread-safe.
     * 
     * @param topic    the topic the message is posted in
     * @param message  the message    
     */
    public void scheduleForDelivery(String topic, M message) {
        messageQueue.offer(new MessageEnvelope<M>(topic, message));
    }

    /**
     * Starts delivery of pending messages to the subscriber.
     * The delivery is stopped once the queue is empty or when an exception
     * occurs.
     * <p>
     * This method is thread-safe and may be called from multiple threads.
     * However, only one thread is delivering messages to the subscriber
     * simultaneously.
     * This method is non-waiting, i.e., if antother thread is already
     * delivering messages, it returns immediately.
     */
    public void deliver() {
        // try to acquire lock.  If not successfull, some other thread is publishing already
        if (!lock.tryLock()) {
            log.log(Level.FINER, "tryLock failed in {0}, Thread #{1}",
                    new Object[]{this, Thread.currentThread().getId()});
            return;
        }
        while (hasPending()) {
            MessageEnvelope<M> msg = messageQueue.peek();
            // try delivery
            try {
                subscriber.receiveMessage(msg.topic(), msg.message());
            } catch (Exception e) {
                log.log(Level.WARNING,
                        "Error delivering msg on topic {0} to {1}, attempt {3}: {2}",
                        new Object[]{msg.topic(), subscriber, e.getMessage(), failedAttempts+1});
                handleError(e);
                lock.unlock();
                return;
            }
            // succeeded
            messageQueue.poll();
            failedAttempts = 0;
        }
        lock.unlock();
    }

    private void handleError(Exception e) {
        failedAttempts++;
        if (failedAttempts >= maxAttempts) {
            log.log(Level.SEVERE,
                    "Repeated failing of delivering msg to {0}: {1}",
                    new Object[]{subscriber, e.getMessage()});
            if (failingSubCB != null)
                failingSubCB.isFailing();
        }
    }

    /**
     * Indicates whether the subscriber has pending messages to receive.
     * 
     * @return  {@code true} if messages are pending
     */
    public boolean hasPending() {
        return !messageQueue.isEmpty();
    }

    /**
     * Gets the numbr of failed attempts to deliver the current message to the
     * subscriber.
     * 
     * @return   the number of failed attempts
     */
    public long failedAttempts() {
        return failedAttempts;
    }

    /**
     * Sets the callback to be called when a subscriber is repeatedly failing
     * to receive a message.
     * 
     * @param callback  the callback
     */
    public void setSubscriberFailingCallbck(SubscriberFailingCallback callback) {
        this.failingSubCB = callback;
    }

    /**
     * Sets the maximum attempts to deliver a message until the failing
     * callback is called.
     * 
     * @param maxAttempts  the maximum number of attempts
     */
    public void setMaxAttempts(long maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public String toString() {
        return "MultiTopicDelivery{" + "subscriber=" + subscriber + 
                ", interested in=" + subscriber.interestedInTopics() +
                ", failedAttempts=" + failedAttempts + ", pendingMessages=" + messageQueue.size() + '}';
    }

}
