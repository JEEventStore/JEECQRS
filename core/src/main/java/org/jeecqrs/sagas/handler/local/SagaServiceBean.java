package org.jeecqrs.sagas.handler.local;

import javax.ejb.EJB;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaCommitIdGenerationStrategy;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;
import org.jeecqrs.sagas.SagaFactory;
import org.jeecqrs.sagas.SagaRepository;

/**
 *
 * @param <E>  the base event type
 */
public class SagaServiceBean<E> implements SagaService<E> {

    @EJB(name="sagaRepository")
    private SagaRepository sagaRepository;

    @EJB(name="sagaConfigResolver")
    private SagaConfigResolver<E> sagaConfigResolver;

    @Override
    public void handle(Class<? extends Saga<E>> sagaClass, String sagaId, E event) {
        SagaConfig<E> config = sagaConfigResolver.configure(sagaClass);
        Saga<E> saga = loadSaga(sagaClass, sagaId);
	if (saga == null) {
	    saga = createNewInstance(sagaClass, sagaId, config);
            saga.handle(event);
            String commitId = commitId(saga, event, config);
            this.sagaRepository().add(saga, commitId);
        } else {
            if (saga.isCompleted())
                return;
            saga.handle(event);
            String commitId = commitId(saga, event, config);
            this.sagaRepository().save(saga, commitId);
        }
    }

    protected SagaRepository<E> sagaRepository() {
        return this.sagaRepository;
    }

    protected Saga<E> loadSaga(Class<? extends Saga<E>> sagaClass, String sagaId) {
	return this.sagaRepository().sagaOfIdentity(sagaClass, sagaId);
    }

    protected String commitId(Saga<E> saga, E event, SagaConfig<E> config) {
        SagaCommitIdGenerationStrategy<E> strat = config.sagaCommitIdGenerationStrategy();
        return strat.generateCommitId(saga, event);
    }

    protected Saga<E> createNewInstance(
            Class<? extends Saga<E>> sagaClass,
            String sagaId,
            SagaConfig<E> sagaConfig) {
        SagaFactory<E> factory = sagaConfig.sagaFactory();
        return factory.createSaga(sagaId);
    }

}
