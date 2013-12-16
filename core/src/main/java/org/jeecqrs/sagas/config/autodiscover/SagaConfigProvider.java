package org.jeecqrs.sagas.config.autodiscover;

import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;

/**
 * Provides the ability to provide saga configs.
 */
public interface SagaConfigProvider<E> {

    /**
     * Specifies the saga class this provider configures.
     * 
     * @return  the class
     */
    Class<? extends Saga<E>> sagaClass();

    /**
     * Provides the saga config.
     * 
     * @return  the saga config
     */
    SagaConfig<E> sagaConfig();
    
}
