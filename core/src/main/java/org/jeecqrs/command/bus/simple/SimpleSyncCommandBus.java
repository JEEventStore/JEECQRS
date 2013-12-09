package org.jeecqrs.command.bus.simple;

import org.jeecqrs.command.CommandBus;

/**
 * A command bus that calls command handlers directly, but asynchronously.
 * Deploy as stateless bean.
 */
public class SimpleSyncCommandBus<C> extends AbstractSimpleCommandBus<C> implements CommandBus<C> {

    @Override
    public void send(C command) {
        this.callHandler(command);
    }

}
