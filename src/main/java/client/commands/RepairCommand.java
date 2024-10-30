package client.commands;

import client.request.Request;
import client.robots.Robot;

/**
 * The RepairCommand class represents a command to repair a robot.
 */
public class RepairCommand extends Command{

    /**
     * Constructs a new RepairCommand.
     */
    public RepairCommand() {
        super("repair");
    }

    @Override
    public Request execute(Robot target) {
        return new Request(target.getName(), "repair", new String[]{});
    } 
}
