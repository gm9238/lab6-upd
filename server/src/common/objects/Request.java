package common.objects;

import java.io.Serializable;

public class Request implements Serializable {
    private String command;
    private Serializable commandObject;

    public Request(String command) {
        this.command = command;
    }

    public String getCommandString() {
        return command;
    }

    public Object getCommandObject() {
        return commandObject;
    }

    public boolean isEmpty() {
        return command.isEmpty();
    }

    @Override
    public String toString() {
        return "Request[" + command + "]";
    }
}
