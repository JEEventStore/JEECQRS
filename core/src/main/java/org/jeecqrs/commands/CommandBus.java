package org.jeecqrs.commands;

/**
 *
 */
public interface CommandBus<C> {

    void send(String bucketId, C command);
    
}