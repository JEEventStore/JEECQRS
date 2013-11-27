package org.jeecqrs.commands.bus.simple;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import org.jeecqrs.commands.CommandBus;
import org.jeecqrs.commands.CommandHandler;
import org.jeecqrs.commands.CommandHandlerNotFoundException;
import org.jeecqrs.commands.CommandHandlerRegistry;

/**
 * Base for a command bus that calls command handlers directly.
 */
public abstract class AbstractSimpleCommandBus<C> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @EJB(name="commandHandlerRegistry")
    private CommandHandlerRegistry registry;

    protected void callHandler(C command) {
        CommandHandler<C> handler = registry.commandHandlerFor(command.getClass());
        if (handler == null)
            throw new CommandHandlerNotFoundException(command.getClass());
        handler.handleCommand(command);
    }

}
