package org.jeecqrs.event;

import java.util.HashSet;
import java.util.Set;

/**
 * A builder for {@link EventInterest}s.
 * 
 * @param <E>  the event base type
 */
public class EventInterestBuilder<E> {

    private final Set<Class<? extends E>> interests = new HashSet<>();

    /**
     * Adds the given event type to the interests.
     * 
     * @param type  the event type
     * 
     * @return this
     */
    public EventInterestBuilder<E> add(Class<? extends E> type) {
        interests.add(type);
        return this;
    }

    /**
     * Builds a new {@link EventInterest} that includes exactly the specified types.
     * 
     * @return  the interest
     */
    public EventInterest<E> build() {
        return new EventInterest<>(interests);
    }
    
}
