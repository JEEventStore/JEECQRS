package org.jeecqrs.commands;

/**
 *
 */
public interface CommandHandlerRegistry<C> {
    
    /**
     * 
     * @param <T>
     * @param clazz
     * @return 
     * @throws CommandHandlerNotFoundException  if no handler can be found
     */
    <T> CommandHandler<T> commandHandlerFor(Class<T> clazz);
    
}
