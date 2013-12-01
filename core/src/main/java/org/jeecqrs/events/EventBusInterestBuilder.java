package org.jeecqrs.events;

import java.util.HashSet;
import java.util.Set;

/**
 * A builder for {@link EventBusInterest}s.
 * 
 * @param <E>  the event base type
 */
public class EventBusInterestBuilder<E> {

    private final Set<Class<? extends E>> interests = new HashSet<>();

    /**
     * Adds the given event type to the interests.
     * 
     * @param type  the event tpe
     * 
     * @return this
     */
    public EventBusInterestBuilder<E> add(Class<? extends E> type) {
        interests.add(type);
        return this;
    }

    /**
     * Builds a new {@link EventBusInterest} that includes exactly the specified types.
     * 
     * @return  the interest
     */
    public EventBusInterest<E> build() {
        return new EventBusInterest<>(interests);
    }
    
}
