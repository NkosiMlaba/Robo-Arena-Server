package client.commands;

import client.robots.Robot;
import client.request.Request;

/**
 * Represents a command to quit or exit.
 * Extends the Command class.
 */
public class QuitCommand extends Command {

    /**
     * Constructs a QuitCommand object.
     * Sets the command name to "quit".
     */
    public QuitCommand() {
        super("quit");
    }

   @Override
   public Request execute(Robot target) {
       String robotName = (target != null) ? target.getName() : "";
       return new Request(robotName, "quit", new String[]{});
   }
}
