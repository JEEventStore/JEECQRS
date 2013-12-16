package org.jeecqrs.sagas;

import java.util.UUID;

/**
 * Generates a unique commitId using {@link UUID#randomUUID()}.
 */
public class RandomCommitIdGenerator<E> implements SagaCommitIdGenerationStrategy<E> {

    @Override
    public String generateCommitId(Saga<E> saga, E event) {
        return UUID.randomUUID().toString();
    }
    
}
