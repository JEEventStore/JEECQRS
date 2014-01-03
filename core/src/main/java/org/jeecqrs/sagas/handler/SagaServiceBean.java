/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.sagas.handler;

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
	if (saga != null) {
            if (saga.isCompleted())
                return;
            saga.handle(event);
            String commitId = commitId(saga, event, config);
            this.sagaRepository().save(saga, commitId);
        } else {
            saga = createNewInstance(sagaClass, sagaId, config);
            saga.handle(event);
            String commitId = commitId(saga, event, config);
            this.sagaRepository().add(saga, commitId);
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
