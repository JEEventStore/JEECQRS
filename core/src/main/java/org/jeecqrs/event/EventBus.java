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

/**
 * Provides the ability to send events to interested listeners (publish-subscribe).
 * The EventBus provides the following guarantees as long as the system is running:
 * <ul>
 * <li>at least once delivery, i.e., each event is published to each subscriber at least once.</li>
 * <li>in-order-guarantee per topic, i.e., for each topic, listeners can expect events to
 *  be delivered in the order they arrived on that particular topic.</li>
 * </ul>
 * Note that events may get lost when the system is crashing or restarted.
 * The CQRS system therefore expects the event store to replay events on system startup.
 * 
 * @param <E>   the base message type
 */
public interface EventBus<E> {

    /**
     * Dispatches and event on the event bus.
     * 
     * @param event     the event to dispatch
     */
    void dispatch(E event);
    
}
