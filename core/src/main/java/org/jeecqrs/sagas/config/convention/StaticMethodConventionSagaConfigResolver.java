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
