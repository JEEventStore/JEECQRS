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

import java.util.Set;
import java.util.logging.Level;
import org.jeecqrs.sagas.handler.SagaService;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.jeecqrs.event.EventInterest;
import org.jeecqrs.event.registry.AbstractEventBusListenerRegistry;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;
import org.jeecqrs.sagas.SagaRegistry;

/**
 *
 */
public class RegisterSagaHandlersEventBusListenerRegistry<E> extends AbstractEventBusListenerRegistry<E> {

    private final Logger log =Logger.getLogger(
            RegisterSagaHandlersEventBusListenerRegistry.class.getName());
    
    @EJB(name="sagaRegistry")
    private SagaRegistry<E> sagaRegistry;

    @EJB(name="sagaService")
    private SagaService sagaService;

    @EJB(name="sagaConfigResolver")
    private SagaConfigResolver<E> sagaConfigResolver;
    
    @PostConstruct
    public void startup() {
        log.info("Registering event listeners for sagas");
        Set<Class<? extends Saga<E>>> sagas = sagaRegistry.allSagas();
        if (sagas.isEmpty())
            log.info("No sagas found");
        else
            registerAll(sagas);
    }

    protected void registerAll(Set<Class<? extends Saga<E>>> sagas) {
        for (Class<? extends Saga<E>> sagaClass : sagas)
            register(sagaClass);
    }

    protected void register(Class<? extends Saga<E>> sagaClass) {
        SagaConfig<? extends Saga<E>, E> config = sagaConfigResolver.configure(sagaClass);
        log.log(Level.INFO, "Registering {0} for {1}",
                new Object[]{sagaClass.getSimpleName(), buildEventLogString(config.interestedInEvents())});
        this.register(new SagaEventBusListener(sagaClass, config, sagaService));
    }

    private String buildEventLogString(EventInterest<E> interest) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int c = 0;
        for (Class<? extends E> cls : interest.interestEventTypes()) {
            if (c++ > 0)
                builder.append(", ");
            builder.append(cls.getSimpleName());
        }
        builder.append("]");
        return builder.toString();
    }
    
}
