package org.jeecqrs.event;

/**
 * Provides the ability to express an interest in events.
 * 
 * @param <E>  the base event type
 */
public interface ExpressEventInterest<E> {
    
    /**
     * Specifies which events are of interest.
     * 
     * @return  the event interest
     */
    EventInterest<E> interestedInEvents();
    
}
