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
