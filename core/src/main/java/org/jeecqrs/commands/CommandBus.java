package org.jeecqrs.commands;

/**
 *
 */
public interface CommandBus<C> {

    void send(C command);
    
}
