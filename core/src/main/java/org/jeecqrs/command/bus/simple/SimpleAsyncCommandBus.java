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

import java.io.Serializable;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.TimedObject;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.jeecqrs.command.CommandBus;

/**
 * A command bus that calls command handlers directly, but asynchronously.
 * Deploy as stateless bean.
 */
public class SimpleAsyncCommandBus<C extends Serializable> extends AbstractSimpleCommandBus<C>
        implements CommandBus<C>, TimedObject {

    private static final Logger log = Logger.getLogger(SimpleAsyncCommandBus.class.getName());

    @Resource
    private TimerService timerService;
    
    @Resource(name = "retryInterval")
    private long retryInterval = 100;

    @Override
    public void send(C command) {
        TimerConfig config = new TimerConfig(command, true);
        timerService.createIntervalTimer(0, retryInterval, config);
    } 
    
    @Timeout
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Override
    public void ejbTimeout(Timer timer) {
        try {
            C command = (C) timer.getInfo();
            this.callHandler(command);
            // if we reach this line, no exception was thrown, i.e., the
            // command was handled successfully and the timer can be cancelled
            timer.cancel();
        } catch (Exception e) {
            log.severe("Error calling command handler: " + e.getMessage());
        }
    }

}
