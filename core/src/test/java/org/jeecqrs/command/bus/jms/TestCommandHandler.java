package org.jeecqrs.command.bus.jms;

import javax.ejb.Stateless;
import org.jeecqrs.command.CommandHandler;
import org.jeecqrs.command.registry.autodiscover.AutoDiscoveredCommandHandler;

@Stateless
public class TestCommandHandler implements AutoDiscoveredCommandHandler<TestCommand> {

    @Override
    public void handle(TestCommand command) {
        System.out.println("Received command: " + command.payload);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Override
    public Class<? extends TestCommand> handledCommandType() {
        return TestCommand.class;
    }
    
}
