package org.jeecqrs.sagas.handler.local;

import javax.ejb.EJB;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaRepository;

/**
 *
 */
public class SagaService<E> {

    @EJB(name="sagaRepository")
    private SagaRepository sagaRepository;

    public void handle(Class<? extends Saga<E>> sagaClass, String sagaId, E event) {
        Saga<E> saga = loadSaga(sagaClass, sagaId);
	if (saga == null) {
	    saga = SagaUtil.createInstance(sagaClass);
            saga.handle(event);
            sagaRepository.add(saga);
        } else {
            saga.handle(event);
            sagaRepository.save(saga);
        }
    }

    protected Saga<E> loadSaga(Class<? extends Saga<E>> sagaClass, String sagaId) {
	return sagaRepository.sagaOfIdentity(sagaClass, sagaId);
    }

}
