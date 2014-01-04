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
public class SagaConfigBuilder<S extends Saga<E>, E> {

    private SagaFactory<S> sagaFactory;
    private SagaIdentificationStrategy<S, E> identStrategy;
    private SagaCommitIdGenerationStrategy<S, E> commitIdStrategy;
    private EventInterest<E> eventInterest;

    public SagaConfigBuilder<S, E> setSagaFactory(SagaFactory<S> factory) {
        this.sagaFactory = factory;
        return this;
    }

    public SagaConfigBuilder<S, E> setSagaIdentificationStrategy(SagaIdentificationStrategy<S, E> strategy) {
        this.identStrategy = strategy;
        return this;
    }

    public SagaConfigBuilder<S, E> setSagaCommitIdGenerationStrategy(SagaCommitIdGenerationStrategy<S, E> strategy) {
        this.commitIdStrategy = strategy;
        return this;
    }

    public SagaConfigBuilder<S, E> setEventInterest(EventInterest<E> interest) {
        this.eventInterest = interest;
        return this;
    }

    public SagaConfig<S, E> build() {
        if (sagaFactory == null)
            throw new IllegalStateException("sagaFactory must not be null");
        if (identStrategy == null)
            throw new IllegalStateException("identStrategy must not be null");
        if (commitIdStrategy == null)
            throw new IllegalStateException("commitIdStrategy must not be null");
        if (eventInterest == null)
            throw new IllegalStateException("eventInterest must not be null");
        return new DefaultSagaConfig<>(sagaFactory, identStrategy,
                commitIdStrategy, eventInterest);
    }
    
}
