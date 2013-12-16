/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.event;

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
