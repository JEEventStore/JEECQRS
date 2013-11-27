package org.jeecqrs.commands.bus.jms;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 *
 */
@MessageDriven(
        mappedName = "commandQueue",  // required on Glassfish
        activationConfig =  {
            // Glassfish: muss property.Name sein, darf / nicht enthalten
            // Jboss: muss jndi name sein, mandatory
            @ActivationConfigProperty(propertyName = "destinatio", propertyValue = "commandQueue")
            //@ActivationConfigProperty(
             //       propertyName = "destinationLookup",
              //      propertyValue = "commandQueue") 

        }
)
public class TestCommandHandler implements MessageListener {

    @Resource
    private MessageDrivenContext mdc;

    @PostConstruct
    public void postConstruct() {
        System.out.println("##################### Post construct called!");
        System.out.println(mdc.getContextData().values());
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("JMSCommandHandler: received message: " + message);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("Ready processed");
    }

    

    
}
