package org.jeecqrs.event;

import java.util.Set;

public interface EventBusListenerRegistry<E> {
    
    Set<EventBusListener<E>> eventListenersFor(Class<? extends E> clazz);

    /**
     * Returns set of all registered event bus listeners.
     * 
     * @return  the set of registered event bus listeners
     */
    Set<EventBusListener<E>> allListeners();
    
}
