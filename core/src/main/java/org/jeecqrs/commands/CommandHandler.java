package org.jeecqrs.commands;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface CommandHandler<C> {

    void handleCommand(C command);
    Class<? extends C> handledCommandType();
    
}
