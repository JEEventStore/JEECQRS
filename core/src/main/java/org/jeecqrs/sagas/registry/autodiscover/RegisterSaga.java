package org.jeecqrs.sagas.registry.autodiscover;

import org.jeecqrs.sagas.Saga;

/**
 * Provides the ability to register a saga.
 * Implementing classes must provide a public default constructor.
 */
public interface RegisterSaga<E> {

    /**
     * Specifies the saga class to be registered.
     * 
     * @return  the class
     */
    Class<? extends Saga<E>> sagaClass();

}
