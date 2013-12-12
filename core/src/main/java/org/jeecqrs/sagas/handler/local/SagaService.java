package org.jeecqrs.sagas.handler.local;

import org.jeecqrs.sagas.Saga;

/**
 *
 */
public interface SagaService<C, E> {

    void handle(Class<? extends Saga<C, E>> sagaClass, String sagaId, E event);

}
