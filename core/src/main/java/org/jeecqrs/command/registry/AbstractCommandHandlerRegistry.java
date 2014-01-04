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

package org.jeecqrs.command.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import org.jeecqrs.command.CommandHandlerRegistry;
import org.jeecqrs.command.CommandHandler;

public abstract class AbstractCommandHandlerRegistry<C> implements CommandHandlerRegistry<C> {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    private final Map<Class<? extends C>, CommandHandler<C>> handlers = new HashMap<>();

    @Override
    @Lock(LockType.READ)
    public CommandHandler<C> commandHandlerFor(Class<? extends C> clazz) {
        return lookup(clazz);
    }

    @Override
    @Lock(LockType.READ)
    public Set<CommandHandler<C>> allCommandHandlers() {
        return new HashSet<>(handlers.values());
    }

    /**
     * Must only be called from {@code LockType.WRITE} protected methods.
     */
    protected void register(Class<? extends C> commandType, CommandHandler<C> handler) {
        log.log(Level.INFO, "Registering CommandHandler for class {0}: {1}",
                new Object[]{ commandType.getCanonicalName(), handler });
        if (lookup(commandType) != null)
            throw new IllegalStateException("Handler for " + commandType.getCanonicalName() + 
                    " already registered");
        handlers.put(commandType, handler);
    }

    /**
     * May be called from {@code LockType.READ} protected methods.
     */
    protected CommandHandler<C> lookup(Class<? extends C> clazz) {
        return handlers.get(clazz);
    }

}
