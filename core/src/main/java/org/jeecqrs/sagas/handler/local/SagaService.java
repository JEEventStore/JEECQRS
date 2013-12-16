package org.jeecqrs.sagas.handler.local;

import org.jeecqrs.sagas.Saga;

/**
 *
 */
public interface SagaService<E> {

    void handle(Class<? extends Saga<E>> sagaClass, String sagaId, E event);

}
