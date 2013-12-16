package org.jeecqrs.sagas;

import org.jeecqrs.event.ExpressEventInterest;

/**
 *
 */
public interface SagaConfig<E> extends ExpressEventInterest<E> {

    /**
     * Specifies a factory to create new instances of the saga.
     * @return 
     */
    SagaFactory<E> sagaFactory();
    
    /**
     * Gets the strategy to identify sagas for incoming events.
     * 
     * @return  the strategy
     */
    SagaIdentificationStrategy<E> sagaIdentificationStrategy();

    /**
     * Gets the strategy to generate a commitId for sagas that handled an event.
     * 
     * @return  the strategy
     */
    SagaCommitIdGenerationStrategy<E> sagaCommitIdGenerationStrategy();
    
}
