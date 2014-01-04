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

package org.jeecqrs.sagas.config;

import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaCommitIdGenerationStrategy;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaFactory;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
public class DefaultSagaConfig<S extends Saga<E>, E> implements SagaConfig<S, E> {

    private final SagaFactory<S> sagaFactory;
    private final SagaIdentificationStrategy<S, E> identStrategy;
    private final SagaCommitIdGenerationStrategy<S, E> commitIdStrategy;
    private final EventInterest<E> eventInterest;

    public DefaultSagaConfig(
            SagaFactory<S> sagaFactory,
            SagaIdentificationStrategy<S, E> identStrategy,
            SagaCommitIdGenerationStrategy<S, E> commitIdStrategy,
            EventInterest<E> eventInterest) {

        this.sagaFactory = sagaFactory;
        this.identStrategy = identStrategy;
        this.commitIdStrategy = commitIdStrategy;
        this.eventInterest = eventInterest;
    }

    @Override
    public SagaFactory<S> sagaFactory() {
        return sagaFactory;
    }

    @Override
    public SagaIdentificationStrategy<S, E> sagaIdentificationStrategy() {
        return identStrategy;
    }

    @Override
    public SagaCommitIdGenerationStrategy<S, E> sagaCommitIdGenerationStrategy() {
        return commitIdStrategy;
    }

    @Override
    public EventInterest<E> interestedInEvents() {
        return eventInterest;
    }
    
}
