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

package org.jeecqrs.sagas.tracker;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jeecqrs.event.EventBus;
import org.jeecqrs.sagas.SagaTracker;

public abstract class AbstractSagaTracker<E> implements SagaTracker<E> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    // beware! should be an event bus that delivers to a persistence layer AND should use persistent queues
    // to avoid lost events
    @EJB(name="eventBus")
    private EventBus<E> eventBus;

    /**
     * Dispatches the timed event to the event bus to make it available
     * to the persistence store.
     * 
     * @param event 
     */
    protected void dispatch(E event) {
        eventBus.dispatch(event);
    }

    protected Date timeoutDate(long timeout) {
        int span = (int) timeout;
        if (timeout != span)
            throw new IllegalStateException(
                    "Timespan too big, integer overflow in Java's Calendar: " + timeout + " != " + span);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MILLISECOND, span);
        return cal.getTime();
    }

}
