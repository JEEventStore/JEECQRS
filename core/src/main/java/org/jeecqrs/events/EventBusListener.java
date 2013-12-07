package org.jeecqrs.events;


/**
 * Provides the ability to listen to events that have been published
 * on the {@link EventBus}.
 * 
 * @param <E>  the base event type
 */
public interface EventBusListener<E> {

    /**
     * Receives an event that has been dispatched on the event bus.
     * 
     * @param event  the event to receive
     */
    void receiveEvent(E event);

    /**
 * Specifies which events a listener wants to receive on the event bus.
     * 
     * @return  the event bus interest
     */
    EventInterest<E> interest();
    
}
