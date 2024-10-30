package domain.commands;


import java.util.HashMap;

import domain.ClientHandler;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.response.*;
import domain.world.Robot;
import domain.world.World;
import domain.ClientController;


public class ReloadCommand extends Command{
    /**
     * Constructs a reload command
     */
    public ReloadCommand() {
        super("reload");
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        Robot robot= clientHandler.getRobot();
        robot.setShots(robot.getMaxShots());
        robot.setStatus(this.getName().toUpperCase());
        return new StandardResponse(new HashMap<>() {{ put("message",  "Done"); }}, robot.getState(), "robot");

    }
    
    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
