package org.jeecqrs.commands;

/**
 *
 */
public interface CommandHandler<C> {

    void handleCommand(C command);
    Class<? extends C> handledCommandType();
    
}
