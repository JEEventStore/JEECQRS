package org.jeecqrs.sagas.registry.autodiscover;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.registry.AbstractSagaRegistry;

/**
 *
 */
public class AutoDiscoverSagaRegistry<E> extends AbstractSagaRegistry<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaRegistry.class.getName());

    @Inject
    private Instance<Saga<E>> sagaInstances;

    @PostConstruct
    public void startup() {
        log.info("Scanning sagas...");
	Iterator<Saga<E>> it = select(sagaInstances);
        if (!it.hasNext())
            log.warning("No sagas found");
	while (it.hasNext()) {
            Saga<E> s = it.next();
            log.fine("Discovered saga: " + s.getClass());
            this.register((Class<? extends Saga<E>>) s.getClass());
	}
    }

    protected Iterator<Saga<E>> select(Instance<Saga<E>> instances) {
        return instances.iterator();
    }
    
}