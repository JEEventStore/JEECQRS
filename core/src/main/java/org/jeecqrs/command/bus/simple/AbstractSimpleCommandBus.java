/*
 * Copyright (c) 2013 Red Rainbow IT Solutions GmbH, Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.jeecqrs.command.bus.simple;

import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import org.jeecqrs.command.CommandBus;
import org.jeecqrs.command.CommandHandler;
import org.jeecqrs.command.CommandHandlerNotFoundException;
import org.jeecqrs.command.CommandHandlerRegistry;

/**
 * Base for a command bus that calls command handlers directly.
 * Can be deployed as stateless bean.
 */
public abstract class AbstractSimpleCommandBus<C> {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @EJB(name="commandHandlerRegistry")
    private CommandHandlerRegistry registry;

    protected void callHandler(C command) {
        CommandHandler<C> handler = registry.commandHandlerFor(command.getClass());
        if (handler == null)
            throw new CommandHandlerNotFoundException(command.getClass());
        handler.handleCommand(command);
    }

}
