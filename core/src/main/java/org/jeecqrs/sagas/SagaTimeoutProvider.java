package org.jeecqrs.sagas;

/**
 * Provides the ability to request saga timeouts.
 */
public interface SagaTimeoutProvider<E> {

    /**
     * Requests a new timeout.
     * 
     * @param request  the timeout request
     */
    void requestTimeout(SagaTimeoutRequest<E> request);

}
