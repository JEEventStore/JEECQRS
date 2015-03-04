package org.jeecqrs.command.registry.autodiscover;

import org.jeecqrs.command.CommandHandler;

/**
 *
 * Marker interface to mark command handlers that shall be automatically discovered.
 * @param <C> the command type handled by this handler.
 */
public interface AutoDiscoveredCommandHandler<C> extends CommandHandler<C> {
    
    Class<C> handledCommandType();
    
}
