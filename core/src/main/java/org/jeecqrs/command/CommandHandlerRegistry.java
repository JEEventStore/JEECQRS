package org.jeecqrs.command;

import java.util.Set;

/**
 *
 */
public interface CommandHandlerRegistry<C> {
    
    /**
     * 
     * @param clazz
     * @return 
     * @throws CommandHandlerNotFoundException  if no handler can be found
     */
    CommandHandler<C> commandHandlerFor(Class<? extends C> clazz);

    /**
     * Returns set of all registered command handlers.
     * @return 
     */
    Set<CommandHandler<C>> allCommandHandlers();
    
}
