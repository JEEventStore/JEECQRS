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

package org.jeecqrs.command.bus.jms;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.jeecqrs.command.CommandHandler;
import org.jeecqrs.command.CommandHandlerRegistry;

public class JMSCommandBusListener implements MessageListener {

    private final static Logger log = Logger.getLogger(JMSCommandBusListener.class.getName());

    @Resource
    private MessageDrivenContext mdc;

    @EJB(name="commandHandlerRegistry")
    private CommandHandlerRegistry registry;

    @Override
    public void onMessage(Message message) {
        log.fine("JMSCommandHandler: received message: " + message);
        Object command = null;
        try {
            command = ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            log.severe("Cannot get command from JMS queue: " + e.getMessage());
            e.printStackTrace();
            mdc.setRollbackOnly();
            return;
        }
        CommandHandler handler = registry.commandHandlerFor(command.getClass());
        if (handler == null) {
            log.severe("No command handler registered for command " 
                    + command.getClass().getName());
            mdc.setRollbackOnly();
            return;
        }
        try {
            handler.handleCommand(command);
        } catch (Exception e) {
            log.severe("Error calling command handler: " +  e.getMessage());
            mdc.setRollbackOnly();
            return;
        }
    }
    
}
