package org.jeecqrs.sagas.handler.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.jeecqrs.sagas.Saga;

/**
 *
 */
public class SagaUtil {

    public static <T extends Saga<?>> T createInstance(Class<T> sagaClass) {
        try {
            Constructor<?> constr = sagaClass.getDeclaredConstructor(new Class<?>[]{});
            constr.setAccessible(true);
            return (T) constr.newInstance(new Object[]{});
	} catch (InstantiationException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
	    throw new RuntimeException("Cannot instantiate object " + 
                    sagaClass + ": " + e.getMessage(), e);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException("Cannot find default constructor for class " + sagaClass);
	}
    }
    
}
