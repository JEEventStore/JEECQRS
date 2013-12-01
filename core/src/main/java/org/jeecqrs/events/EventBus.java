package org.jeecqrs.events;

/**
 * Provides the ability to send events to interested listeners (publish-subscribe).
 * 
 * @param <E>   the base message type
 */
public interface EventBus<E> {

    /**
     * Dispatches and event in the given bucket on the event bus.
     * 
     * @param bucketId  the bucket in which the event shall be sent
     * @param event     the event to dispatch
     */
    void dispatch(String bucketId, E event);
    
}
