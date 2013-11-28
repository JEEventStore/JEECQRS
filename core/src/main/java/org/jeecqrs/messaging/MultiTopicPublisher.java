package org.jeecqrs.messaging;

/**
 * Publish-subscribe messaging system publisher supporting multiple topics
 * per subscriber.
 * Implementations must be thread-safe.
 */
public interface MultiTopicPublisher<M> {

    /**
     * Publishes a {@code message} in a given {@code topic}.
     * 
     * @param topic    the topic the message
     * @param message  the message to be published
     */
    void publish(String topic, M message);

    /**
     * Subscribes an interested listener.
     * 
     * @param subscriber  the subscriber, not null, not subscribed already
     */
    void subscribe(MultiTopicSubscriber<M> subscriber);

    /**
     * Unsubscribes a subscriber.
     * 
     * @param subscriber  the subscriber to unsubscribe, not null, subscribed
     */
    void unsubscribe(MultiTopicSubscriber<M> subscriber);
    
}
