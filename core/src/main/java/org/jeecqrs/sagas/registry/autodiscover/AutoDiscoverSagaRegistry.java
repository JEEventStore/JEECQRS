package org.jeecqrs.sagas.registry.autodiscover;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.config.autodiscover.SagaConfigProvider;
import org.jeecqrs.sagas.registry.AbstractSagaRegistry;

/**
 * Automatically discovers and registers Sagas using {@link SagaConfigProvider}.
 */
public class AutoDiscoverSagaRegistry<E> extends AbstractSagaRegistry<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaRegistry.class.getName());

    @Inject
    private Instance<RegisterSaga<E>> instances;

    @PostConstruct
    public void startup() {
        log.info("Scanning sagas...");
	Iterator<RegisterSaga<E>> it = select(instances);
        if (!it.hasNext())
            log.warning("No sagas found");
	while (it.hasNext()) {
            RegisterSaga<E> r = it.next();
            Class<? extends Saga<E>> sagaClass = r.sagaClass();
            log.fine("Discovered saga: " + sagaClass);
            this.register(sagaClass);
	}
    }

    protected Iterator<RegisterSaga<E>> select(Instance<RegisterSaga<E>> instances) {
        return instances.iterator();
    }
    
}