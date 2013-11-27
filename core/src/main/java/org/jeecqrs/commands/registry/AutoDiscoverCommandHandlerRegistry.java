package org.jeecqrs.commands.registry;

import java.util.Iterator;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import org.jeecqrs.commands.CommandHandler;

/**
 *
 */
public class AutoDiscoverCommandHandlerRegistry<C> extends AbstractCommandHandlerRegistry<C> {

    private Logger log = Logger.getLogger(AutoDiscoverCommandHandlerRegistry.class.getName());

    @Inject
    private Instance<CommandHandler> handlerInstances;

    @PostConstruct
    public void startup() {
        log.info("Scanning command handlers");
	Iterator<CommandHandler> it = handlerInstances.iterator();
        if (!it.hasNext()) {
            log.warning("No CommandHandlers found");
        }
	while (it.hasNext()) {
            CommandHandler h = it.next();
            log.fine("Discovered CommandHandler: "+ h);
            this.register(h.handledCommandType(), h);
	}
    }
    
}
