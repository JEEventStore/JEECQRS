package org.jeecqrs.command.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.command.CommandHandlerRegistry;
import org.jeecqrs.command.CommandHandler;

public abstract class AbstractCommandHandlerRegistry<C> implements CommandHandlerRegistry<C> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final Map<Class<? extends C>, CommandHandler<C>> handlers = new HashMap<>();

    @Override
    @Lock(LockType.READ)
    public CommandHandler<C> commandHandlerFor(Class<? extends C> clazz) {
        return lookup(clazz);
    }

    @Override
    @Lock(LockType.READ)
    public Set<CommandHandler<C>> allCommandHandlers() {
        return new HashSet<>(handlers.values());
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     */
    protected void register(CommandHandler<C> handler) {
        Class<? extends C> clazz = handler.handledCommandType();
        log.log(Level.INFO, "Registering CommandHandler for class {0}: {1}",
                new Object[]{clazz.getCanonicalName(), handler});
        if (lookup(clazz) != null)
            throw new IllegalStateException("Handler for " + clazz.getCanonicalName() + " already registered");
        handlers.put(clazz, handler);
    }

    /**
     * May be called from {@code LockType.READ} protected methods.
     */
    protected CommandHandler<C> lookup(Class<? extends C> clazz) {
        return handlers.get(clazz);
    }

}
