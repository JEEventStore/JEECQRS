package org.jeecqrs.events;

/**
 * Provides the ability to send events to interested listeners (publish-subscribe).
 * 
 * @param <E>   the base message type
 */
public interface EventBus<E> {

    void dispatch(String bucketId, E event);
    
}
