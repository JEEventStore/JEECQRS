package org.jeecqrs.event;


/**
 * Provides the ability to listen to events that have been published
 * on the {@link EventBus}.
 * 
 * @param <E>  the base event type
 */
public interface EventBusListener<E> extends ExpressEventInterest<E> {

    /**
     * Receives an event that has been dispatched on the event bus.
     * 
     * @param event  the event to receive
     */
    void receiveEvent(E event);
    
}
