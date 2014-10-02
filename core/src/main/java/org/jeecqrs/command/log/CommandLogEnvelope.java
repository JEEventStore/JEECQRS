package org.jeecqrs.command.log;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.Validate;

public class CommandLogEnvelope<C> implements Serializable {

    private final String type;
    private final C command;
    private final Date occuredOn = new Date();
    private Map<String, String> metadata;

    public CommandLogEnvelope(C command, Map<String, String> headers) {
        Validate.notNull(command, "command must not be null");
        Validate.notNull(headers, "headers must not be null");
        this.command = command;
        this.type = command.getClass().getCanonicalName();
        this.metadata = Collections.unmodifiableMap(headers);
    }

    public CommandLogEnvelope(C command) {
        this(command, new HashMap<String, String>());
    }

    public String type() {
        return type;
    }

    public C command() {
        return command;
    }

    public Date occuredOn() {
        return new Date(occuredOn.getTime());
    }

    public Map<String, String> getHeaders() {
        return metadata;
    }

    @Override
    public String toString() {
        return "CommandLogEnvelope{" + 
                "type=" + type + 
                ", occuredOn=" + occuredOn + "(" + occuredOn.getTime() + ")" +
                ", headers=" + metadata + 
                ", command=" + command + 
                '}';
    }
    
}
