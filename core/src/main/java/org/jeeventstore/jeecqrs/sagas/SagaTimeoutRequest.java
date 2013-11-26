package org.jeeventstore.jeecqrs.sagas;

/**
 * Specifies a request to publish a given event after a given time span.
 */
public interface SagaTimeoutRequest<E> {

    /**
     * Identifies the bucket to which the saga requesting the timeout belongs.
     * 
     * @return  the identifier
     */
    String bucketId();

    /**
     * Identifies the saga that requested the timeout.
     * 
     * @return  the identifier
     */
    String sagaId();

    /**
     * Gets the time span after which the given event shall be published in milliseconds.
     * There is a guarantee that the event will not be published before the
     * specified time span has passed, but the actual publication of the
     * event might occur later and is implementation dependent.
     * 
     * @return number of milliseconds after which the event is to be published
     */
    long timeout();

    /**
     * A user-defined, human readable description of the timeout request.
     * Very useful for debugging purposes when inspecting the tracker database.
     * 
     * @return a human readable description of the timeout request
     */
    String description();

    /**
     * Gets the event to be published when the timeout occurred.
     * 
     * @return  the event to be published
     */
    E event();
    
}
