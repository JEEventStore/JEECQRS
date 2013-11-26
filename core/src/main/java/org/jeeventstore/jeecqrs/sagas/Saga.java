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

package org.jeeventstore.jeecqrs.sagas;

import java.util.List;

/**
 * A saga is a long running process that reacts to events and issues commands.
 * Sagas are aware of the notion of time and can request that they are
 * notified again after a given interval.
 * <p>
 * The flow is as follows:
 * For each type of a saga, a listener subscribes to the event dispatching
 * system and listens for domain events the saga is interested in.
 * If an event of interest is received, the listener loads the corresponding
 * saga from the {@link SagaRepository} and sends the event to the saga's event
 * handler {@link #handle}.  The saga handles the event and may issue a number
 * of commands as well as request timeouts, which allow to publish events
 * after a specified interval.  The saga's new state is then saved into the
 * repository again, the commands are sent through the command bus and the
 * requested timeouts are sent to the {@link SagaTracker}, which issues
 * the events after the specified timeout.
 * <p>
 * The implementation
 * of the saga is up to the client, but it is often a good idea to
 * use some kind of state-machine approach to achieve idem-potency for events,
 * since events can be delivered multiple times (the event dispatching system
 * has an at-least-once guarantee).
 * <p>
 * @param <C>   the type of commands to be sent
 * @param <E>   the type of events to handle
 */
public interface Saga<C, E> {

    /**
     * Identifies the bucket to which the saga belongs.
     * 
     * @return  the identifier
     */
    String bucketId();

    /**
     * Uniquely identifies the saga within the bucket.
     * 
     * @return  the identifier
     */
    String sagaId();

    /**
     * Handles an event.
     * 
     * @param event  the event to handle
     */
    void handle(E event);

    /**
     * Gets the list of commands the saga issued while handling the event.
     * 
     * @return  the list of issued commands
     */
    List<C> issuedCommands();

    /**
     * Clears the list of issued commands.
     */
    void clearIssuedCommands();

    /**
     * Gets the list of timeouts the saga requested while handling the event.
     * 
     * @return  the list of requested timeouts.
     */
    List<SagaTimeoutRequest<E>> requestedTimeouts();

    /**
     * Clears the list of requested timeouts.
     */
    void clearRequestedTimeouts();
    
}
