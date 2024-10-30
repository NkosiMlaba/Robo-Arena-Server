package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.ClientController;
import domain.response.*;
import domain.world.Robot;
import domain.world.World;
import json.JsonHandler;

public class TurnCommand extends Command{

    /**
     * Constructs a TurnCommand object with the specified argument.
     *
     * @param argument The argument representing the direction to turn the robot ("right" or "left").
     */
    public TurnCommand(String argument) {
        super("turn", argument);
    }

    /**
     * Sends information about the turning robot to all clients.
     * 
     * @param clientHandler The client handler of the turning robot robot.
     */
    public void broadcastToOtherRobots(ClientHandler clientHandler, Robot robot) {
        // broadcast to other clients
        for (ClientHandler cH : ClientHandler.getClientHandlers()) {
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

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        World world = clientHandler.getWorld();
        Robot robot = clientHandler.getRobot();
        Boolean turnRight = getArgument().equals("right")? true : false;
        world.updateDirection(robot, turnRight);
        robot.setStatus("NORMAL");

        broadcastToOtherRobots(clientHandler, robot);

        return new StandardResponse(new HashMap<>() {{ put("message", "Done"); }}, robot.getState(), "robot");
    }
    

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
