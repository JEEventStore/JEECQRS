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
