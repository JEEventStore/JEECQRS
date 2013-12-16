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

package org.jeecqrs.sagas.handler.local;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.jeecqrs.event.EventBusListener;
import org.jeecqrs.event.EventBusListenerRegistry;
import org.jeecqrs.event.registry.AbstractEventBusListenerRegistry;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;
import org.jeecqrs.sagas.SagaRegistry;

/**
 *
 */
public class RegisterSagaHandlersEventBusListenerRegistry<E> extends AbstractEventBusListenerRegistry<E> {

    private final Logger log = Logger.getLogger(RegisterSagaHandlersEventBusListenerRegistry.class.getName());
    
    @EJB(name="listenerRegistry")
    private EventBusListenerRegistry<E> delegateRegistry;

    @EJB(name="sagaRegistry")
    private SagaRegistry<E> sagaRegistry;

    @EJB(name="sagaService")
    private SagaService sagaService;

    @EJB(name="sagaConfigResolver")
    private SagaConfigResolver<E> sagaConfigResolver;
    
    @PostConstruct
    public void startup() {
        log.info("Registering event listeners from delegate registry");
        for (EventBusListener<E> ebl : delegateRegistry.allListeners())
            this.register(ebl);
        log.info("Registering event listeners for sagas");
        for (Class<? extends Saga<E>> sagaClass : sagaRegistry.allSagas()) {
            SagaConfig<E> config = sagaConfigResolver.configure(sagaClass);
            SagaEventBusListener<E> sebl = new SagaEventBusListener<>(sagaClass,
                    config, sagaService);
            this.register(sebl);
        }
    }
    
}
