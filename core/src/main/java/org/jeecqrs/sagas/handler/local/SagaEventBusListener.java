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

package org.jeecqrs.sagas.handler.local;

import org.jeecqrs.sagas.handler.SagaService;
import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
class SagaEventBusListener<S extends Saga<E>, E> implements EventBusListener<E> {

    private final Class<S> sagaClass;
    private final SagaConfig<S, E> sagaConfig;
    private final SagaService<S, E> sagaService;

    public SagaEventBusListener(
            Class<S> sagaClass,
            SagaConfig<S, E> sagaConfig,
            SagaService<S, E> sagaService) {

        this.sagaClass = sagaClass;
        this.sagaConfig = sagaConfig;
        this.sagaService = sagaService;
    }

    @Override
    public void receiveEvent(E event) {
        SagaIdentificationStrategy<S, E> strat = sagaConfig.sagaIdentificationStrategy();
        String sagaId = strat.identifySaga(event);
        sagaService.handle(sagaClass, sagaId, event);
    }

    @Override
    public EventInterest<E> interestedInEvents() {
        return sagaConfig.interestedInEvents();
    }

    @Override
    public String toString() {
        return super.toString() + "[saga=" + sagaClass.getSimpleName() + "]";
    }
    
}
