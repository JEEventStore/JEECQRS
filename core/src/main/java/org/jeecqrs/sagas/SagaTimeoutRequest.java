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

/**
 * Specifies a request to publish a given event after a given time span.
 * 
 * @param <E>   the type of events to handle
 */
public class SagaTimeoutRequest<E> {

    private String sagaId;
    private long timeout;
    private String description;
    private E event;

    public SagaTimeoutRequest(
            String sagaId,
            long timeout,
            String description,
            E event) {

        if (sagaId == null)
            throw new NullPointerException("sagaId must not be null");
        if (sagaId.trim().isEmpty())
            throw new IllegalArgumentException("sagaId must not be empty");
        if (event == null)
            throw new NullPointerException("event must not be null");

        this.sagaId = sagaId;
        this.timeout = timeout;
        this.description = description;
        this.event = event;
    }

    /**
     * Identifies the saga that requested the timeout.
     * 
     * @return  the identifier
     */
    public String sagaId() {
        return sagaId;
    }

    /**
     * Gets the time span after which the given event shall be published in milliseconds.
     * There is a guarantee that the event will not be published before the
     * specified time span has passed, but the actual publication of the
     * event might occur later and is implementation dependent.
     * 
     * @return number of milliseconds after which the event is to be published
     */
    public long timeout() {
	return timeout;
    }

    /**
     * A user-defined, human readable description of the timeout request.
     * Very useful for debugging purposes when inspecting the tracker database.
     * 
     * @return a human readable description of the timeout request
     */
    public String description() {
        return description;
    }

    /**
     * Gets the event to be published when the timeout occurred.
     * 
     * @return  the event to be published
     */
    public E event() {
	return event;
    }
    
}
