package org.jeecqrs.command.bus.simple;

import javax.ejb.Asynchronous;
import org.jeecqrs.command.CommandBus;

/**
 * A command bus that calls command handlers directly, but asynchronously.
 * Deploy as stateless bean.
 */
public class SimpleAsyncCommandBus<C> extends AbstractSimpleCommandBus<C> implements CommandBus<C> {

    @Override
    @Asynchronous
    public void send(C command) {
        this.callHandler(command);
    }

}
