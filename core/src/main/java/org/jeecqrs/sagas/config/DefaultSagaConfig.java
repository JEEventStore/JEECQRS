package org.jeecqrs.sagas.config;

import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.SagaCommitIdGenerationStrategy;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaFactory;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
public class DefaultSagaConfig<E> implements SagaConfig<E> {

    private final SagaFactory<E> sagaFactory;
    private final SagaIdentificationStrategy<E> identStrategy;
    private final SagaCommitIdGenerationStrategy<E> commitIdStrategy;
    private final EventInterest<E> eventInterest;

    public DefaultSagaConfig(
            SagaFactory<E> sagaFactory,
            SagaIdentificationStrategy<E> identStrategy,
            SagaCommitIdGenerationStrategy<E> commitIdStrategy,
            EventInterest<E> eventInterest) {

        this.sagaFactory = sagaFactory;
        this.identStrategy = identStrategy;
        this.commitIdStrategy = commitIdStrategy;
        this.eventInterest = eventInterest;
    }

    @Override
    public SagaFactory<E> sagaFactory() {
        return sagaFactory;
    }

    @Override
    public SagaIdentificationStrategy<E> sagaIdentificationStrategy() {
        return identStrategy;
    }

    @Override
    public SagaCommitIdGenerationStrategy<E> sagaCommitIdGenerationStrategy() {
        return commitIdStrategy;
    }

    @Override
    public EventInterest<E> interestedInEvents() {
        return eventInterest;
    }
    
}
