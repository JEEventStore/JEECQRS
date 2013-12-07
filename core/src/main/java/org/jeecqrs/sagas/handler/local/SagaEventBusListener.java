package org.jeecqrs.sagas.handler.local;

import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventInterest;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaIdentificationStrategy;

/**
 *
 */
class SagaEventBusListener<E> implements EventBusListener<E> {

    private final Class<? extends Saga<E>> sagaClass;
    private final EventInterest<E> interest;
    private final SagaIdentificationStrategy<E> identStrategy;
    private final SagaService<E> sagaService;

    SagaEventBusListener(
            Class<? extends Saga<E>> sagaClass,
            EventInterest<E> interest,
            SagaIdentificationStrategy<E> identStrategy,
            SagaService<E> sagaService) {
        
        this.sagaClass = sagaClass;
        this.interest = interest;
        this.identStrategy = identStrategy;
        this.sagaService = sagaService;
    }

    @Override
    public void receiveEvent(E event) {
        String sagaId = identStrategy.identifySaga(event);
        sagaService.handle(sagaClass, sagaId, event);
    }

    @Override
    public EventInterest<E> interest() {
        return this.interest;
    }
    
}
