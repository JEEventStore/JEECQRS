package org.jeecqrs.sagas;

import java.util.UUID;

/**
 * Generates a unique commitId using {@link UUID#randomUUID()}.
 */
public class RandomCommitIdGenerator<C, E> implements SagaCommitIdGenerationStrategy<C, E> {

    @Override
    public String generateCommitId(Saga<C, E> saga, E event) {
        return UUID.randomUUID().toString();
    }
    
}
