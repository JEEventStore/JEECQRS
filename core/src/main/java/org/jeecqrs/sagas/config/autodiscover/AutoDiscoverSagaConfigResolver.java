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

/**
 * Resolves saga configs by searching for suitable {@link SagaConfigProvider}s.
 * Deploy as Singleton bean.
 */
public class AutoDiscoverSagaConfigResolver<E> implements SagaConfigResolver<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaConfigResolver.class.getName());

    private final Map<Class<? extends Saga<E>>, SagaConfig<? extends Saga<E>, E>> registry = new HashMap<>();

    @Inject
    private Instance<SagaConfigProvider<? extends Saga<E>, E>> providers;

    @PostConstruct
    public void startup() {
        log.info("Scanning saga config providers...");
	Iterator<SagaConfigProvider<? extends Saga<E>, E>> it = select(providers);
        if (!it.hasNext())
            log.warning("No saga config providers found");
	while (it.hasNext()) {
            SagaConfigProvider<? extends Saga<E>, E> provider = it.next();
            Class<? extends Saga<E>> sagaClass = provider.sagaClass();
            log.log(Level.INFO, "Discovered saga config provider {0} for saga {1}",
                    new Object[]{provider.getClass(), sagaClass});
            SagaConfig<? extends Saga<E>, E> config = provider.sagaConfig();
            if (config == null)
                throw new IllegalStateException("Provider must not return null SagaConfig");
            register(sagaClass, config);
	}
    }

    protected Iterator<SagaConfigProvider<? extends Saga<E>, E>> select(
            Instance<SagaConfigProvider<? extends Saga<E>, E>> providers) {
        return providers.iterator();
    }

    /**
     * Registers the given SagaConfig for the given Saga class.
     * Must only be called from LockType.WRITE protected methods.
     * 
     * @param clazz   the saga class
     * @param config  the saga config
     */
    protected void register(Class<? extends Saga<E>> clazz, SagaConfig<? extends Saga<E>, E> config) {
        registry.put(clazz, config);
    }

    @Override
    @Lock(LockType.READ)
    public SagaConfig<? extends Saga<E>, E> configure(Class<? extends Saga<E>> sagaType) {
        return registry.get(sagaType);
    }
    
}
