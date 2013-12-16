package org.jeecqrs.sagas;

/**
 * Specifies the ability to resolve configuration properties for sagas.
 */
public interface SagaConfigResolver<E> {

    SagaConfig<E> configure(Class<? extends Saga<E>> sagaType);

}
