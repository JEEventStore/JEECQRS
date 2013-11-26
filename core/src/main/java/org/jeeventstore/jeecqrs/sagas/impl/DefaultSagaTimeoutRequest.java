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

package org.jeeventstore.jeecqrs.sagas.impl;

import org.jeeventstore.jeecqrs.sagas.SagaTimeoutRequest;

/**
 * Default implementation of {@link SagaTimeoutRequest}.
 * 
 * @param <E>   the type of events to handle
 */
public class DefaultSagaTimeoutRequest<E> implements SagaTimeoutRequest<E> {
    
    private String bucketId;
    private String sagaId;
    private long timeout;
    private String description;
    private E event;

    public DefaultSagaTimeoutRequest(
            String bucketId,
            String sagaId,
            long timeout,
            String description,
            E event) {

        if (bucketId == null)
            throw new NullPointerException("bucketId must not be null");
        if (sagaId == null)
            throw new NullPointerException("sagaId must not be null");
        if (sagaId.trim().isEmpty())
            throw new IllegalArgumentException("sagaId must not be empty");
        if (event == null)
            throw new NullPointerException("event must not be null");

        this.bucketId = bucketId;
        this.sagaId = sagaId;
        this.timeout = timeout;
        this.description = description;
        this.event = event;
    }

    @Override
    public String bucketId() {
        return bucketId;
    }

    @Override
    public String sagaId() {
        return sagaId;
    }

    @Override
    public long timeout() {
	return timeout;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public E event() {
	return event;
    }
    
}
