package org.jeecqrs.events.registry.autodiscover;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.events.EventBusListener;
import org.jeecqrs.events.registry.AbstractEventBusListenerRegistry;

/**
 *
 */
public class AutoDiscoverEventBusListenerRegistry<E> extends AbstractEventBusListenerRegistry<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverEventBusListenerRegistry.class.getName());

    @Inject
    private Instance<EventBusListener<E>> handlerInstances;

    @PostConstruct
    public void startup() {
        log.info("Scanning event bus listeners");
	Iterator<EventBusListener<E>> it = select(handlerInstances);
        if (!it.hasNext())
            log.warning("No event bus listeners found");
	while (it.hasNext()) {
            EventBusListener<E> h = it.next();
            log.fine("Discovered event bus listener: " + h);
            this.register(h);
	}
    }

    protected Iterator<EventBusListener<E>> select(Instance<EventBusListener<E>> instances) {
        return instances.iterator();
    }
    
}
