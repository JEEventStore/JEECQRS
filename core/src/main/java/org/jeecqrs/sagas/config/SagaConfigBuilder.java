package org.jeecqrs.sagas.config;

import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.SagaCommitIdGenerationStrategy;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaFactory;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
public class SagaConfigBuilder<E> {

    private SagaFactory<E> sagaFactory;
    private SagaIdentificationStrategy<E> identStrategy;
    private SagaCommitIdGenerationStrategy<E> commitIdStrategy;
    private EventInterest<E> eventInterest;

    public SagaConfigBuilder<E> setSagaFactory(SagaFactory<E> factory) {
        this.sagaFactory = factory;
        return this;
    }

    public SagaConfigBuilder<E> setSagaIdentificationStrategy(SagaIdentificationStrategy<E> strategy) {
        this.identStrategy = strategy;
        return this;
    }

    public SagaConfigBuilder<E> setSagaCommitIdGenerationStrategy(SagaCommitIdGenerationStrategy<E> strategy) {
        this.commitIdStrategy = strategy;
        return this;
    }

    public SagaConfigBuilder<E> setEventInterest(EventInterest<E> interest) {
        this.eventInterest = interest;
        return this;
    }

    public SagaConfig<E> build() {
        if (sagaFactory == null)
            throw new IllegalStateException("sagaFactory must not be null");
        if (identStrategy == null)
            throw new IllegalStateException("identStrategy must not be null");
        if (commitIdStrategy == null)
            throw new IllegalStateException("commitIdStrategy must not be null");
        if (eventInterest == null)
            throw new IllegalStateException("eventInterest must not be null");
        return new DefaultSagaConfig<E>(sagaFactory, identStrategy,
                commitIdStrategy, eventInterest);
    }
    
}
