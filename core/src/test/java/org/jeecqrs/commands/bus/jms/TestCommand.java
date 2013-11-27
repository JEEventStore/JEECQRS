package org.jeecqrs.commands.bus.jms;

import java.io.Serializable;

public class TestCommand implements Serializable {

    public String payload;

    public TestCommand(String payload) {
        this.payload = payload;
    }
    
}
