package org.jeecqrs.command.registry.autodiscover;

import org.jeecqrs.command.CommandHandler;

/**
 *
 */
public interface AutoDiscoveredCommandHandler<C> extends CommandHandler<C> {
    
    Class<? extends C> handledCommandType();
    
}
