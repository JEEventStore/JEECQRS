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

/**
 * The SagaTracker manages timeout requests requested by Sagas.
 * The default state is disabled, i.e., requests that timed out are
 * not processed until {@link #enable} has been called.
 * 
 * @param <E>   the type of events to handle
 */
public interface SagaTracker<E> {

    /**
     * Enable the publication of events for requests that have timed out.
     */
    void enablePublication();

    /**
     * Disable the publication of events.
     * New requests (via {@link #requestTimeout) are still persisted to
     * durable storage, but the events are not published until the tracker
     * is enabled.
     */
    void disablePublication();

    /**
     * Request a new timeout.
     * 
     * @param request  the timeout request
     */
    void requestTimeout(SagaTimeoutRequest<E> request);

}
