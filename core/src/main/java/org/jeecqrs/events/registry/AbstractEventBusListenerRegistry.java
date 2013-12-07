package org.jeecqrs.events.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.events.EventInterest;
import org.jeecqrs.events.EventBusListener;
import org.jeecqrs.events.EventBusListenerRegistry;

public class AbstractEventBusListenerRegistry<E> implements EventBusListenerRegistry<E> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final Map<Class<? extends E>, Set<EventBusListener<E>>> handlers = new HashMap<>();

    @Override
    @Lock(LockType.READ)
    public Set<EventBusListener<E>> eventListenersFor(Class<? extends E> clazz) {
        return lookup(clazz);
    }
    
    @Override
    @Lock(LockType.READ)
    public Set<EventBusListener<E>> allListeners() {
        Set<EventBusListener<E>> result = new HashSet<>();
        for (Set<EventBusListener<E>> s : handlers.values())
            result.addAll(s);
        return result;
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     * @param handler
     */
    protected void register(EventBusListener<E> handler) {
        EventInterest<E> interest = handler.interest();
        for (Class<? extends E> c : interest.interestEventTypes())
            register(handler, c);
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     * @param handler
     * @param clazz
     */
    protected void register(EventBusListener<E> handler, Class<? extends E> clazz) {
        log.log(Level.INFO, "Registering {0} for event {1}: {2}",
                new Object[]{EventBusListener.class.getName(),
                    clazz.getName(), handler});
        Set<EventBusListener<E>> set = handlers.get(clazz);
        if (set == null) {
            set = new HashSet<>();
            handlers.put(clazz, set);
        }
        set.add(handler);
    }

    /**
     * May be called from {@code LockType.READ} protected methods.
     * @param clazz
     * @return 
     */
    protected Set<EventBusListener<E>> lookup(Class<? extends E> clazz) {
        Set<EventBusListener<E>> res = handlers.get(clazz);
        return res == null ? null : Collections.unmodifiableSet(res);
    }

}
