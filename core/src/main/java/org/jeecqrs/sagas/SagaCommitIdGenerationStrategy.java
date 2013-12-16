package org.jeecqrs.sagas;

/**
 * Provides the ability to generate a commitId for a SagaRepository transaction.
 * 
 * @param <E>
 */
public interface SagaCommitIdGenerationStrategy<E> {

    /**
     * Returns a unique commitId to save the saga that has handled the given event.
     * 
     * @param saga  the saga that has handled the event
     * @param event  the event
     * @return   the commitId
     */
    String generateCommitId(Saga<E> saga, E event);
    
}
