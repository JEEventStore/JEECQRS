package org.jeecqrs.messaging.local;

import org.jeecqrs.messaging.MultiTopicSubscriber;

/**
 * Provides the ability to inform about failing subscribers.
 * 
 * @param <M>  the message type
 */
public interface SubscriberFailingCallback<M> {

    void isFailing(MultiTopicSubscriber<M> subscriber);
    
}
