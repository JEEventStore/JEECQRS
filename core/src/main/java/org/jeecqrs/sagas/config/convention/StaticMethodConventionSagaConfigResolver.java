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

package org.jeecqrs.sagas.config.convention;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.sagas.Saga;
import org.jeecqrs.sagas.SagaConfig;
import org.jeecqrs.sagas.SagaConfigResolver;

/**
 * Resolves the configuration of a saga based on static methods on the saga class.
 * Deploy as Singleton bean
 * @param <E>
 */
public class StaticMethodConventionSagaConfigResolver<E> implements SagaConfigResolver<E> {
    
    private final static String CONFIG_METHOD_NAME = "configure";
    private final Map<Class<? extends Saga<E>>, SagaConfig<E>> cache = new ConcurrentHashMap<>();

    /**
     * Gets the strategy to generate a commitId for sagas that handled an event.
     * We allow READ locking for efficiency and handle locking internally
     * 
     * @param sagaType
     * @return  the strategy
     */
    @Override
    @Lock(LockType.READ)
    public SagaConfig<E> configure(Class<? extends Saga<E>> sagaType) {
        SagaConfig<E> config = cache.get(sagaType);
        if (config == null) {
            // no need to synchronize, because result is unique
            try {
                Method em = findMethod(sagaType, CONFIG_METHOD_NAME);
                config = (SagaConfig<E>) em.invoke(null, new Object[]{});
                if (config == null)
                    throw new IllegalStateException(
                            sagaType.getName() + "#" + CONFIG_METHOD_NAME + " must not return null");
                cache.put(sagaType, config);
            } catch (IllegalAccessException | IllegalArgumentException |
                    IllegalStateException | InvocationTargetException e) {
                throw new RuntimeException("Error obtaining saga config: " + e.getMessage(), e);
            }
        }
        return config;
    }

    private Method findMethod(Class<? extends Saga<E>> sagaType, String name) {
        try {
            return sagaType.getDeclaredMethod(name, new Class[]{});
        } catch (NoSuchMethodException | SecurityException e) {
            String msg = String.format("Error calling method %s#%s: %s",
                    sagaType.getName(), name, e.getMessage());
            throw new RuntimeException(msg, e);
        }
    }
    
}
