package org.jeecqrs.command;

/**
 *
 */
public interface CommandHandler<C> {

    void handleCommand(C command);
    Class<? extends C> handledCommandType();
    
}
