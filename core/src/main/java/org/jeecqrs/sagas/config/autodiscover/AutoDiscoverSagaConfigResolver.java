package org.jeecqrs.sagas.config.autodiscover;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;
import org.jeecqrs.sagas.registry.autodiscover.AutoDiscoverSagaRegistry;

/**
 * Resolves saga configs by searching for suitable {@link SagaConfigProvider}s.
 * Deploy as Singleton bean.
 */
public class AutoDiscoverSagaConfigResolver<E> implements SagaConfigResolver<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaConfigResolver.class.getName());

    private final Map<Class<? extends Saga<E>>, SagaConfig<E>> registry = new HashMap<>();

    @Inject
    private Instance<SagaConfigProvider<E>> providers;

    @PostConstruct
    public void startup() {
        log.info("Scanning saga config providers...");
	Iterator<SagaConfigProvider<E>> it = select(providers);
        if (!it.hasNext())
            log.warning("No saga config providers found");
	while (it.hasNext()) {
            SagaConfigProvider<E> provider = it.next();
            Class<? extends Saga<E>> clazz = provider.sagaClass();
            log.log(Level.INFO, "Discovered saga config provider {0} for saga {1}",
                    new Object[]{provider.getClass(), clazz});
            SagaConfig<E> config = provider.sagaConfig();
            if (config == null)
                throw new IllegalStateException("Provider must not return null SagaConfig");
            register(clazz, config);
	}
    }

    protected Iterator<SagaConfigProvider<E>> select(Instance<SagaConfigProvider<E>> providers) {
        return providers.iterator();
    }

    /**
     * Registers the given SagaConfig for the given Saga class.
     * Must only be called from LockType.WRITE protected methods.
     * 
     * @param clazz   the saga class
     * @param config  the saga config
     */
    protected void register(Class<? extends Saga<E>> clazz, SagaConfig<E> config) {
        registry.put(clazz, config);
    }

    @Override
    @Lock(LockType.READ)
    public SagaConfig<E> configure(Class<? extends Saga<E>> sagaType) {
        return registry.get(sagaType);
    }
    
}
