package org.jeecqrs.sagas;

import java.util.Set;

public interface SagaRegistry<E> {
    
    /**
     * Returns set of all registered sagas.
     * 
     * @return  the sagas
     */
    Set<Class<? extends Saga<E>>> allSagas();
    
}
