package org.jeecqrs.commands.bus.jms;

import java.io.File;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jeecqrs.commands.CommandBus;
import org.jeecqrs.commands.registry.AutoDiscoverCommandHandlerRegistry;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 */
public class JMSCommandBusTest extends Arquillian {
    
    @Deployment
    public static Archive<?> deployment() {
        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addAsModule(ShrinkWrap.create(JavaArchive.class, "ejb.jar")
                        .addAsManifestResource(new File("src/test/resources/META-INF/beans.xml"))
                        .addAsManifestResource(new File(
                                "src/test/resources/META-INF/ejb-jar-JMSCommandBusTest.xml"),
                                "ejb-jar.xml")
                        .addPackage(AutoDiscoverCommandHandlerRegistry.class.getPackage())
                        .addPackage(JMSCommandBus.class.getPackage())
                        .addPackage(CommandBus.class.getPackage())
                );
        return ear;
    }

    @EJB(lookup = "java:global/test/ejb/CommandBus")
    private CommandBus commandBus;

    @Test
    public void testSend() {
        for (int i = 0; i < 50; i++)
            commandBus.send(new TestCommand("Hello, World"));
        System.out.println("Warte 10 Sekundne, bis MDB fertig ist");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("Test fertgi");
    }
    
}
