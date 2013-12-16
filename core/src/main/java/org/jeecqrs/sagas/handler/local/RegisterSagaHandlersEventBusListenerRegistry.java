package org.jeecqrs.sagas.handler.local;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventBusListenerRegistry;
import org.jeecqrs.event.registry.AbstractEventBusListenerRegistry;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;
import org.jeecqrs.sagas.SagaRegistry;

/**
 *
 */
public class RegisterSagaHandlersEventBusListenerRegistry<E> extends AbstractEventBusListenerRegistry<E> {

    private final Logger log = Logger.getLogger(RegisterSagaHandlersEventBusListenerRegistry.class.getName());
    
    @EJB(name="listenerRegistry")
    private EventBusListenerRegistry<E> delegateRegistry;

    @EJB(name="sagaRegistry")
    private SagaRegistry<E> sagaRegistry;

    @EJB(name="sagaService")
    private SagaService sagaService;

    @EJB(name="sagaConfigResolver")
    private SagaConfigResolver<E> sagaConfigResolver;
    
    @PostConstruct
    public void startup() {
        log.info("Registering event listeners from delegate registry");
        for (EventBusListener<E> ebl : delegateRegistry.allListeners())
            this.register(ebl);
        log.info("Registering event listeners for sagas");
        for (Class<? extends Saga<E>> sagaClass : sagaRegistry.allSagas()) {
            SagaConfig<E> config = sagaConfigResolver.configure(sagaClass);
            SagaEventBusListener<E> sebl = new SagaEventBusListener<>(sagaClass,
                    config, sagaService);
            this.register(sebl);
        }
    }
    
}
