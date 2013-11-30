package org.jeecqrs.commands.bus.simple;

import javax.ejb.Asynchronous;
import org.jeecqrs.commands.CommandBus;

/**
 * A command bus that calls command handlers directly, but asynchronously.
 */
public class SimpleAsyncCommandBus<C> extends AbstractSimpleCommandBus<C> implements CommandBus<C> {

    @Override
    @Asynchronous
    public void send(String bucketId, C command) {
        this.callHandler(bucketId, command);
    }

}
