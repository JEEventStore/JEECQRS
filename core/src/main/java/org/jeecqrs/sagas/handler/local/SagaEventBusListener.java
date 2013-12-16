package org.jeecqrs.sagas.handler.local;

import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
class SagaEventBusListener<E> implements EventBusListener<E> {

    private final Class<? extends Saga<E>> sagaClass;
    private final SagaConfig<E> sagaConfig;
    private final SagaService<E> sagaService;

    public SagaEventBusListener(
            Class<? extends Saga<E>> sagaClass,
            SagaConfig<E> sagaConfig,
            SagaService<E> sagaService) {

        this.sagaClass = sagaClass;
        this.sagaConfig = sagaConfig;
        this.sagaService = sagaService;
    }

    @Override
    public void receiveEvent(E event) {
        SagaIdentificationStrategy<E> strat = sagaConfig.sagaIdentificationStrategy();
        String sagaId = strat.identifySaga(event);
        sagaService.handle(sagaClass, sagaId, event);
    }

    @Override
    public EventInterest<E> interestedInEvents() {
        return sagaConfig.interestedInEvents();
    }
    
}
