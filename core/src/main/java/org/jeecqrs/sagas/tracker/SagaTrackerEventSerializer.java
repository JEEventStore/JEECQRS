package org.jeecqrs.sagas.tracker;

import java.util.List;

/**
 *
 */
public interface SagaTrackerEventSerializer<E> {

    String serialize(E event);

    /**
     * Serializes into a list of events to allow for event upcasting.
     * @param body
     * @return 
     */
    List<? extends E> deserialize(String body);
    
}
