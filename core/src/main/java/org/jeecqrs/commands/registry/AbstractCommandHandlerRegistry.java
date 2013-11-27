package org.jeecqrs.commands.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.commands.CommandHandlerRegistry;
import org.jeecqrs.commands.CommandHandler;

public class AbstractCommandHandlerRegistry<C> implements CommandHandlerRegistry<C> {

    private Logger log = Logger.getLogger(this.getClass().getName());

    private Map<Class<? extends C>, CommandHandler<? extends C>> handlers = new HashMap<>();

    @Override
    @Lock(LockType.READ)
    public <T> CommandHandler<T> commandHandlerFor(Class<T> clazz) {
        return lookup(clazz);
    }

    @Lock(LockType.WRITE)
    protected void register(
            Class<? extends C> clazz,
            CommandHandler<? extends C> handler) {
        log.info("Registering CommandHandler for class " + clazz.getCanonicalName() + ": "+ handler);
        if (lookup(clazz) != null)
            throw new IllegalStateException("Handler for " + clazz.getCanonicalName() + " already registered");
        handlers.put(clazz, handler);
    }

    protected <T> CommandHandler<T> lookup(Class<T> clazz) {
        return (CommandHandler < T >) handlers.get(clazz);
    }

}
