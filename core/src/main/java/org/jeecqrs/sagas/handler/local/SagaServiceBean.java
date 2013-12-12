package org.jeecqrs.sagas.handler.local;

import javax.ejb.EJB;
import org.jeecqrs.command.CommandBus;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaRepository;
import org.jeecqrs.sagas.SagaTracker;

/**
 *
 */
public class SagaServiceBean<C, E> implements SagaService<C, E> {

    @EJB(name="sagaRepository()")
    private SagaRepository sagaRepository;

    @EJB(name="commandBus")
    private CommandBus<C> commandBus;

    @EJB(name="sagaTracker")
    private SagaTracker<E> sagaTracker;

    @Override
    public void handle(Class<? extends Saga<C, E>> sagaClass, String sagaId, E event) {
        Saga<C, E> saga = loadSaga(sagaClass, sagaId);
	if (saga == null) {
	    saga = createNewInstance(sagaClass, sagaId);
            runSaga(saga, event);
            String commitId = saga.commitIdGenerationStrategy().generateCommitId(saga, event);
            this.sagaRepository().add(saga, commitId);
        } else {
            if (saga.isCompleted())
                return;
            runSaga(saga, event);
            String commitId = saga.commitIdGenerationStrategy().generateCommitId(saga, event);
            this.sagaRepository().save(saga, commitId);
}
    }

    protected void runSaga(Saga<C, E> saga, E event) {
        saga.handle(event);
        saga.publishCommands(commandBus);
        saga.requestTimeouts(sagaTracker);
    }

    protected SagaRepository<C, E> sagaRepository() {
        return this.sagaRepository;
    }

    protected Saga<C, E> loadSaga(Class<? extends Saga<C, E>> sagaClass, String sagaId) {
	return this.sagaRepository().sagaOfIdentity(sagaClass, sagaId);
    }

    protected Saga<C, E> createNewInstance(Class<? extends Saga<C, E>> sagaClass, String sagaId) {
        return SagaUtil.createInstance(sagaClass, sagaId);
    }

}
