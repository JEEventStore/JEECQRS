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

package org.jeecqrs.sagas;

import org.jeecqrs.command.CommandBus;
import org.jeecqrs.event.ExpressEventInterest;

/**
 * A saga is a long running process that reacts to events and issues commands.
 * Sagas are aware of the notion of time and can request a notification after
 * a given interval has elapsed.
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
 * 
 * @param <C>  the base command type
 * @param <E>  the base event type
 */
public interface Saga<C, E> extends ExpressEventInterest<E> {

    /**
     * Uniquely identifies the saga.
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
     * Specifies whether the current saga is completed.
     * Completed sagas may be ignored by the infrastructure for efficiency reasons.
     * 
     * @return <pre>true</pre> if the saga is completed
     */
    boolean isCompleted();


    /**
     * Publishes the raised commands on the given command bus.
     * 
     * @param commandBus the command bus to use
     */
    void publishCommands(CommandBus<C> commandBus);

    /**
     * Requests the timeouts.
     * 
     * @param timeoutProvider  the timeout provider service
     */
    void requestTimeouts(SagaTimeoutProvider<E> timeoutProvider);

    /**
     * Gets the strategy to identify sagas for incoming events.
     * 
     * @return  the strategy
     */
    SagaIdentificationStrategy<E> sagaIdentificationStrategy();

    /**
     * Gets the strategy to generate a commitId for sagas that handled an event.
     * 
     * @return  the strategy
     */
    SagaCommitIdGenerationStrategy<C, E> commitIdGenerationStrategy();
    
}
