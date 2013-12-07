package org.jeecqrs.event;

/**
 * Provides the ability to express an interest in events.
 * 
 * @param <E>  the base event type
 */
public interface ExpressEventInterest<E> {
    
    /**
     * Specifies which events this is interested in.
     * 
     * @return  the event interest
     */
    EventInterest<E> interest();
}
