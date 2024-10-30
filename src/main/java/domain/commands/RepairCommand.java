package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.*;
import domain.world.Robot;
import domain.world.World;
import domain.ClientController;


public class RepairCommand extends Command{

    /**
     * Constructs a repair command
     */
    public RepairCommand() {
        super("repair");
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        Robot robot = clientHandler.getRobot();
        robot.setShiels(robot.getMaxSheilds());
        robot.setStatus(this.getName().toUpperCase());
        return new StandardResponse(new HashMap<>() {{ put("message", "Done"); }}, robot.getState(), "robot");
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
    
}
