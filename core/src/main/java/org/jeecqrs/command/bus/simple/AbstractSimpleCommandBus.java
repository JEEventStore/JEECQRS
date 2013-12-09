package org.jeecqrs.command.bus.simple;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import org.jeecqrs.command.CommandBus;
import org.jeecqrs.command.CommandHandler;
import org.jeecqrs.command.CommandHandlerNotFoundException;
import org.jeecqrs.command.CommandHandlerRegistry;

/**
 * Base for a command bus that calls command handlers directly.
 * Can be deployed as stateless bean.
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
