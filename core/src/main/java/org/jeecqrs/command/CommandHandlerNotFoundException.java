package org.jeecqrs.command;

/**
 *
 */
public class CommandHandlerNotFoundException extends RuntimeException {

    private final Class commandClass;

    public CommandHandlerNotFoundException(Class commandClass) {
        this.commandClass = commandClass;
    }

    @Override
    public String getMessage() {
        return "No command handler for command found: " + commandClass.getCanonicalName();
    }

    public Class getCommandClass() {
        return commandClass;
    }

}
