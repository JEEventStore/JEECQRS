package org.jeecqrs.command.bus.jms;

import javax.ejb.Stateless;
import org.jeecqrs.command.CommandHandler;

@Stateless
public class TestCommandHandler implements CommandHandler<TestCommand> {

    @Override
    public void handleCommand(TestCommand command) {
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
