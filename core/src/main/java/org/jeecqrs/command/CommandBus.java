package org.jeecqrs.command;

/**
 *
 */
public interface CommandBus<C> {

    void send(C command);
    
}
