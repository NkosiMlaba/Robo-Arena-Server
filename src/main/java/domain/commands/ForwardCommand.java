package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.ApiResponse;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.world.Robot;
import domain.world.World;
import domain.world.util.UpdateResponse;
import json.JsonHandler;
import domain.ClientController;


/**
 * Represents a command to move the robot forward a specified number of steps.
 * Extends the Command class.
 */
public class ForwardCommand extends Command {

    /**
     * Constructs a ForwardCommand object with the specified argument.
     *
     * @param argument The argument representing the number of steps to move the robot forward.
     */
    public ForwardCommand(String argument) {
        super("forward", argument);
    }

    /**
     * Sends information about the moving robot to all clients.
     * 
     * @param clientHandler The client handler of the moving robot robot.
     */
    public void broadcastToOtherRobots (ClientHandler clientHandler, Robot robot) {
        // broadcast to other clients
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != clientHandler) { // has launched robot into world.
                Response res = new StandardResponse(new HashMap<>(){{
                    put("message", "enemy state changed");
                    put("robotName", robot.getName());
                    put("robotState", robot.getState());
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }

    /**
     * Sets the message about the movement of the robot.
     *
     * @param robot The robot that is being moved.
     * @param nrSteps The number of steps the robot is being moved forward.
     * @param world The world in which the robot is being moved.
     */
    public String setMessage (Robot robot, int nrSteps, World world) {
        if (world.updatePosition(robot ,nrSteps)[0] == UpdateResponse.SUCCESS) {
            return "Done";
        }
        else if (world.updatePosition(robot ,nrSteps)[0] == UpdateResponse.FAILED_OBSTRUCTED) {
            return "Obstructed";
        }
        else {
            return "Outside safezone";
        }
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        World world = clientHandler.getWorld();
        Robot robot = clientHandler.getRobot();
        int nrSteps = Integer.parseInt(getArgument());
        String message;
        
        robot.setStatus("NORMAL");

        message = setMessage(robot, nrSteps, world);

        broadcastToOtherRobots(clientHandler, robot);

        return new StandardResponse(new HashMap<>() {{ put("message", message); }}, robot.getState(), "robot");
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
