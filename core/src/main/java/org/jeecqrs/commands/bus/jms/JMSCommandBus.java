package org.jeecqrs.commands.bus.jms;

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
import org.jeecqrs.commands.CommandBus;

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
    public void send(String bucketId, C command) {
	log.log(Level.FINER, "send command: " + command.getClass());
        Session session = null;
        try {
            session = createSession(connection);
            MessageProducer publisher = createProducer(session, commandQueue);
            Message message = createMessage(session, command);
            message.setStringProperty("bucketId", bucketId);
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
