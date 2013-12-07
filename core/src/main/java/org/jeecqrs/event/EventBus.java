package org.jeecqrs.event;

/**
 * Provides the ability to send events to interested listeners (publish-subscribe).
 * 
 * @param <E>   the base message type
 */
public interface EventBus<E> {

    /**
     * Dispatches and event on the event bus.
     * 
     * @param event     the event to dispatch
     */
    void dispatch(E event);
    
}
