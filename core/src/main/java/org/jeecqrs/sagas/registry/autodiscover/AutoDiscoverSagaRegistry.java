package org.jeecqrs.sagas.registry.autodiscover;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.registry.AbstractSagaRegistry;

/**
 * Automatically discovers and registers Sagas.
 * All sagas must additionally declare a default constructor (private, protected,
 * default or public).
 */
public class AutoDiscoverSagaRegistry<C, E> extends AbstractSagaRegistry<C, E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaRegistry.class.getName());

    @Inject
    private Instance<Saga<C, E>> sagaInstances;

    @PostConstruct
    public void startup() {
        log.info("Scanning sagas...");
	Iterator<Saga<C, E>> it = select(sagaInstances);
        if (!it.hasNext())
            log.warning("No sagas found");
	while (it.hasNext()) {
            Saga<C, E> s = it.next();
            log.fine("Discovered saga: " + s.getClass());
            this.register((Class<? extends Saga<C, E>>) s.getClass());
	}
    }

    protected Iterator<Saga<C, E>> select(Instance<Saga<C, E>> instances) {
        return instances.iterator();
    }
    
}