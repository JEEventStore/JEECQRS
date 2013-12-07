package org.jeecqrs.events;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Specifies an interest in a set of event types.
 * 
 * @param <E>  the base event type
 */
public class EventInterest<E> {

    private final Set<Class<? extends E>> interests;

    protected EventInterest(Collection<Class<? extends E>> types) {
        this.interests = Collections.unmodifiableSet(new HashSet<>(types));
    }

    /**
     * Gets the set of event types the listener is interested in.
     * 
     * @return  the set of event classes
     */
    public Set<Class<? extends E>> interestEventTypes() {
        return this.interests;
    }

    /**
     * Tells whether the interest includes the given event type.
     * 
     * @param clazz  the event type
     * @return  {@code true} if the interest includes the given even type
     */
    public boolean includes(Class<? extends E> clazz) {
        return this.interests.contains(clazz);
    }

}
