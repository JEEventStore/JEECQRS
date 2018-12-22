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

package org.jeecqrs.event.registry;

import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventBusListenerRegistry;
import org.jeecqrs.event.EventInterest;

import javax.ejb.Lock;
import javax.ejb.LockType;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractEventBusListenerRegistry<E> implements EventBusListenerRegistry<E> {

    private static final Logger log = Logger.getLogger(AbstractEventBusListenerRegistry.class.getName());

    private final Map<Class<? extends E>, Set<EventBusListener<E>>> handlers = new HashMap<>();

    @Override
    @Lock(LockType.READ)
    public Set<EventBusListener<E>> eventListenersFor(Class<? extends E> clazz) {
        return lookup(clazz);
    }
    
    @Override
    @Lock(LockType.READ)
    public Set<EventBusListener<E>> allListeners() {
        Set<EventBusListener<E>> result = new HashSet<>();
        for (Set<EventBusListener<E>> s : handlers.values())
            result.addAll(s);
        return result;
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     * @param handler the event handler
     */
    protected void register(EventBusListener<E> handler) {
        EventInterest<E> interest = handler.interestedInEvents();
        for (Class<? extends E> c : interest.interestEventTypes())
            register(handler, c);
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     * @param handler the event handler
     * @param clazz the class
     */
    protected void register(EventBusListener<E> handler, Class<? extends E> clazz) {
        log.log(Level.FINE, "Register {1} for event {0}",
                new Object[]{ clazz.getSimpleName(), handler});
        Set<EventBusListener<E>> set = handlers.get(clazz);
        if (set == null) {
            set = new HashSet<>();
            handlers.put(clazz, set);
        }
        set.add(handler);
    }

    /**
     * May be called from {@code LockType.READ} protected methods.
     * @param clazz the event class
     * @return the event listeners registered for the given class
     */
    protected Set<EventBusListener<E>> lookup(Class<? extends E> clazz) {
        Set<EventBusListener<E>> res = handlers.get(clazz);
        return res == null ? null : Collections.unmodifiableSet(res);
    }

}
