package org.jeecqrs.commands.bus.simple;

import org.jeecqrs.commands.CommandBus;

/**
 * A command bus that calls command handlers directly, but asynchronously.
 */
public class SimpleSyncCommandBus<C> extends AbstractSimpleCommandBus<C> implements CommandBus<C> {

    @Override
    public void send(String bucketId, C command) {
        this.callHandler(bucketId, command);
    }

}