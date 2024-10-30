package client.commands;

import client.request.Request;
import client.robots.Robot;

/**
 * The ReloadCommand class represents a command to reload a robot.
 */
public class ReloadCommand extends Command{

    /**
     * Constructs a new ReloadCommand.
     */
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public Request execute(Robot target) {
        return new Request(target.getName(), "reload", new String[]{});
    }
    
}
