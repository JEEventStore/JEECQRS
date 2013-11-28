package org.jeecqrs.messaging.local;

/**
 * Provides the ability to inform about failing subscribers.
 */
public interface SubscriberFailingCallback {

    void isFailing();
    
}
