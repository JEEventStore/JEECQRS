package org.jeecqrs.messaging;

import java.util.Set;

/**
 * Publish-subscribe messaging system subscriber supporting multiple topics
 * of interest.
 * 
 * @param <M>   the message interface
 */
public interface MultiTopicSubscriber<M> {

    /**
     * Receives a new message in the given topic.
     * Called upon arrival of a new topic message.
     * Must be thread-safe.
     * 
     * @param topic    the topic the message was published in
     * @param message  the message
     */
    void receiveMessage(String topic, M message);

    /**
     * The publisher has canceled the subscription of this subscriber.
     * Under circumstances the publisher might want to force a detach
     * of a subscriber, for example when the number of retries to deliver a
     * message has been exhausted.  This method is called after the
     * publisher has canceled the subscription and may be used by the client
     * to handle the situation gracefully.
     */
    void canceledSubscription();

    /**
     * Gets the set of topics this subscriber is interested in.
     * Must not change between subscription and unsubscription.
     * 
     * @return  the topics of interest
     */
    Set<String> interestedInTopics();
    
}
