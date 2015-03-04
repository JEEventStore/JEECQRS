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
 * Automatically discovers and registers Sagas using {@link RegisterSaga}.
 */
public class AutoDiscoverSagaRegistry<E> extends AbstractSagaRegistry<E> {

    private final Logger log = Logger.getLogger(AutoDiscoverSagaRegistry.class.getName());

    @Inject
    private Instance<RegisterSaga<? extends Saga<?>, ?>> instances;

    @PostConstruct
    public void startup() {
        log.info("Scanning sagas...");
	Iterator<RegisterSaga<? extends Saga<?>, ?>> it = select(instances);
        if (!it.hasNext())
            log.warning("No sagas found.");
	while (it.hasNext()) {
            this.registerUntyped(it.next());
        }
    }

    // required to convert the untyped versions to the typed versions
    private void registerUntyped(RegisterSaga<? extends Saga<?>, ?> register) {
        this.register(fixType(register));
    }

    private RegisterSaga<? extends Saga<E>, E> fixType(
            RegisterSaga<? extends Saga<?>, ?> in) {
        return (RegisterSaga<? extends Saga<E>, E>) in;
    }

    protected <S extends Saga<E>> void register(RegisterSaga<S, E> register) {
        Class<S> sagaClass = register.sagaClass();
        this.register(sagaClass);
    }

    protected Iterator<RegisterSaga<? extends Saga<?>, ?>> select(
            Instance<RegisterSaga<? extends Saga<?>, ?>> instances) {
        return instances.iterator();
    }
    
}