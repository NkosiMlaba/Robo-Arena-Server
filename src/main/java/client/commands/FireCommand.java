package client.commands;

import client.request.Request;
import client.robots.Robot;


/**
 * Represents a command to fire at a target robot.
 * Extends the Command class.
 */
public class FireCommand extends Command {

    /**
     * Constructs a FireCommand object.
     * Sets the command name to "fire".
     */
    public FireCommand() {
        super("fire");
    }

    @Override
    public Request execute(Robot target) {
        return new Request(target.getName(), "fire", new String[]{});
    }  
}
