package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.ApiResponse;
import domain.response.ErrorResponse;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.world.Robot;
import domain.world.World;
import json.JsonHandler;
import domain.ClientController;

/**
 * Represents a command to fire a bullet from the robot.
 * Extends the Command class.
 */
public class FireCommand extends Command{
    private ClientHandler clientHandler;

    /**
     * Constructs a FireCommand object.
     * Calls the superclass constructor to set the command name.
     */
    public FireCommand() {
        super("fire");
    }

    /**
     * Fires a bullet from the robot and handles the outcome.
     *
     * @param robot The robot object that performs the fire command.
     * @param world The world object representing the game world.
     * @return The response object indicating the outcome of the fire command.
     */
    private Response fire(Robot robot, World world) {
        robot.decreaseShots();
    
        Object[] result = world.fireGun(robot, robot.getBulletDistance());
        if (result.length == 2) { // blocked by another robot.
            Robot robotHit = (Robot) result[1]; // get the robot shot
            int distance = robot.getDistance(robotHit);
    
            processRobotHit(robot, robotHit, distance);
            return createHitResponse(robot, robotHit, distance);
        } else {
            notifyClientsOfMiss(robot);
            return createMissResponse(robot);
        }
    }
    
    /**
     * Processes the event of a robot being hit.
     * 
     * @param robot The robot that fired.
     * @param robotHit The robot that was hit.
     * @param distance The distance between the firing robot and the hit robot.
     */
    private void processRobotHit(Robot robot, Robot robotHit, int distance) {
        decreaseRobotShields(robotHit);
        notifyRobotHit(robotHit);
        notifyClientsOfStateChange(robotHit);
        notifyClientsOfHit(robot, distance);
    }
    
    /**
     * Decreases the shields of the hit robot.
     * 
     * @param robotHit The robot that was hit.
     */
    private void decreaseRobotShields(Robot robotHit) {
        robotHit.decreaseSheilds();
    }
    
    /**
     * Notifies the hit robot that it has been hit.
     * 
     * @param robotHit The robot that was hit.
     */
    private void notifyRobotHit(Robot robotHit) {
        Response robotHitResponse = new StandardResponse(new HashMap<>() {{
            put("message", "You've been shot.");
        }}, robotHit.getState());
        String jsonStr = JsonHandler.serializeResponse(robotHitResponse);
        robotHit.getClientHandler().sendToClient(jsonStr);
    }
    
    /**
     * Notifies all clients about the state change of the hit robot.
     * 
     * @param robotHit The robot that was hit.
     */
    private void notifyClientsOfStateChange(Robot robotHit) {
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != robotHit.getClientHandler()) {
                Response res = new StandardResponse(new HashMap<>() {{
                    put("message", "enemy state changed");
                    put("robotName", robotHit.getName());
                    put("robotState", robotHit.getState());
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }
    
    /**
     * Notifies all clients about the hit event.
     * 
     * @param robot The robot that fired.
     * @param distance The distance to the hit robot.
     */
    private void notifyClientsOfHit(Robot robot, int distance) {
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != robot.getClientHandler()) {
                Response res = new StandardResponse(new HashMap<>() {{
                    put("message", "an enemy fired gun");
                    put("robotName", robot.getName());
                    put("distance", distance);
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }
    
    /**
     * Notifies all clients about a missed shot.
     * 
     * @param robot The robot that fired.
     */
    private void notifyClientsOfMiss(Robot robot) {
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != robot.getClientHandler()) {
                Response res = new StandardResponse(new HashMap<>() {{
                    put("message", "an enemy fired gun");
                    put("robotName", robot.getName());
                    put("distance", robot.getBulletDistance());
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }
    
    /**
     * Creates a response indicating a hit.
     * 
     * @param robot The robot that fired.
     * @param robotHit The robot that was hit.
     * @param distance The distance to the hit robot.
     * @return A response object indicating a successful hit.
     */
    private Response createHitResponse(Robot robot, Robot robotHit, int distance) {
        HashMap<String, Object> data = new HashMap<>() {{
            put("message", "Hit");
            put("distance", distance);
            put("robot", robotHit.getName());
            put("state", robotHit.getState());
        }};
        return new StandardResponse(data, robot.getState(), "robot");
    }
    
    /**
     * Creates a response indicating a miss.
     * 
     * @param robot The robot that fired.
     * @return A response object indicating a miss.
     */
    private Response createMissResponse(Robot robot) {
        return new StandardResponse(new HashMap<>() {{
            put("message", "Miss");
        }}, robot.getState(), "robot");
    }
    
    
    
    @Override
    public Response execute(ClientHandler clientHandlerGiven, World worldGiven) {
        clientHandler = clientHandlerGiven;

        Robot robot = clientHandler.getRobot();
        World world = clientHandler.getWorld();
        robot.setStatus("NORMAL");

        if (robot.getShots() > 0) {
            return fire(robot, world);
        }

        return new ErrorResponse("No bullets in gun");
    }
    
    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
