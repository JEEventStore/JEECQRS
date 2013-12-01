package org.jeecqrs.events;

import java.util.Set;

/**
 * Provides the ability to specify which events a listener wants to
 * receive on the event bus.
 * 
 * @param <E>  the message base type
 */
public interface EventBusInterest<E> {

    /**
     * Gets the set of event types the listener is interested in.
     * 
     * @return  the set of event classes
     */
    Set<Class<? extends E>> interestEventTypes();

    /**
     * Tells whether the interest includes the given event type.
     * 
     * @param clazz  the event type
     * @return  {@code true} if the interest includes the given even type
     */
    boolean includes(Class<? extends E> clazz);

}
