package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.*;
import domain.response.ApiResponse;
import domain.world.Robot;
import domain.world.World;
import domain.world.util.UpdateResponse;
import json.JsonHandler;
import domain.ClientController;


/**
 * Represents a command to move the robot backwards in the world.
 * Extends the Command class and overrides the execute method to execute the back command.
 */
public class BackCommand extends Command {

    /** Constructs a BackCommand object with the specified argument.
     */
    public BackCommand(String argument) {
        super("back", argument);
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        World world = clientHandler.getWorld();
        Robot robot = clientHandler.getRobot();
        int nrSteps = Integer.parseInt(getArgument());
        String message;
        
        robot.setStatus("NORMAL");

        if (world.updatePosition(robot, -nrSteps)[0] == UpdateResponse.SUCCESS) {
            message = "Done";
        }
        else if (world.updatePosition(robot, -nrSteps)[0] == UpdateResponse.FAILED_OBSTRUCTED) {
            message = "Obstructed";
        }
        else {
            message = "Outside safezone";
        }

        // broadcast to other clients
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != clientHandler) {
                Response res = new StandardResponse(new HashMap<>(){{
                    put("message", "enemy state changed");
                    put("robotName", robot.getName());
                    put("robotState", robot.getState());
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
        return new StandardResponse(new HashMap<>() {{ put("message", message); }}, robot.getState(), "robot");
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
