package client.commands;

import client.request.Request;
import client.robots.Robot;

/**
 * Represents a command to look at the surroundings.
 * Extends the Command class.
 */
public class LookCommand extends Command {
    
    /**
     * Constructs a LookCommand object.
     * Sets the command name to "look".
     */
    public LookCommand() {
        super("look");
    }

    @Override
    public Request execute(Robot target) {
        return new Request(target.getName(), "look", new String[] {});
    }
}
