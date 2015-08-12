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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.jeecqrs.command.CommandBus;

public class JMSCommandBus<C extends Serializable> implements CommandBus<C> {

    final static Logger log = Logger.getLogger(JMSCommandBus.class.getCanonicalName());

    @Resource(name="jms/ConnectionFactory")
    private ConnectionFactory connectionFactory;
    
    @Resource(name="jms/commandQueue")
    private Queue commandQueue;

    @Resource
    private SessionContext sessionContext;

    @Resource(name="usePersistentMessages")
    private Boolean usePersistentMessages = false;

    private Connection connection;

    @PostConstruct
    public void makeConnection() {
        try {
            connection = connectionFactory.createConnection();
        } catch (JMSException e) {
            log.severe("Error creating connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(C command) {
	log.log(Level.FINER, "send command: " + command.getClass());
        Session session = null;
        try {
            session = createSession(connection);
            MessageProducer publisher = createProducer(session, commandQueue);
            Message message = createMessage(session, command);
            publisher.send(message);
        } catch (JMSException e) {
            log.log(Level.SEVERE, "Error sending message: {0}", e.getMessage());
            e.printStackTrace();
            sessionContext.setRollbackOnly();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    log.warning("Error '" + e.getErrorCode() + "' closing JMS session: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    protected Session createSession(Connection connection) throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    protected MessageProducer createProducer(Session session, Destination destination) throws JMSException {
        MessageProducer producer = session.createProducer(destination);
        if (usePersistentMessages == null || !usePersistentMessages)
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        return producer;
    }

    protected Message createMessage(Session session, C command) throws JMSException {
        return session.createObjectMessage(command);
    }

    @PreDestroy
    public void tidyUp() throws RuntimeException {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                log.warning("Error '" + e.getErrorCode() + "' closing JMS connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
}
