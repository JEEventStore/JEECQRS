package org.jeecqrs.sagas;

/**
 * Provides the ability to identify the matching saga for a given event.
 * 
 * @param <E>
 */
public interface SagaIdentificationStrategy<E> {

    /**
     * Returns the identity of the saga that handles the given event.
     * 
     * @param event  the event
     * @return   the saga identity
     */
    String identifySaga(E event);
    
}
