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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import org.jeecqrs.sagas.SagaTimeoutRequest;

public abstract class AbstractSerializingSagaTracker<E> extends AbstractSagaTracker<E> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());
    
    @EJB(name="eventSerializer")
    private SagaTrackerEventSerializer serializer;

    @Override
    public void requestTimeout(SagaTimeoutRequest<E> request) {
	log.log(Level.FINE,
		"Schedule timeout in {0}ms for event {1}",
		new Object[]{request.timeout(), request.event().getClass().getSimpleName()});
        E event = request.event();
        String body = serialize(event);
        persist(request.sagaId(), request.description(),
                request.event().getClass().getCanonicalName(),
                body, request.timeout());
    }

    protected String serialize(E event) {
        return serializer.serialize(event);
    }

    protected abstract void persist(
            String sagaId,
            String description,
            String eventType,
            String eventBody,
            long timeout);

    protected void dispatch(String eventBody) {
        List<? extends E> events = this.serializer.deserialize(eventBody);
        for (E e : events)
            this.dispatch(e);
    }

}
