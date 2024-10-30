package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.*;
import domain.world.Robot;
import domain.world.World;
import json.JsonHandler;
import domain.ClientController;


/**
 * Constructs a LaunchCommand object with the specified robot name and arguments.
 *
 * @param robotName The name of the robot to launch.
 * @param args      The arguments containing the robot kind, shields, and shots.
 */
public class QuitCommand extends Command{

    /**
     * Constructs a QuitCommand object.
     * This command is used to disconnect a client from the server.
     */
    public QuitCommand() {
        super("quit");
    }

    /**
     * Sends a quit message to all other connected clients.
     * This method is called when a client disconnects from the server.
     * It broadcasts a message to all other clients informing them about the robot that disconnected.
     *
     * @param clientHandler The ClientHandler object representing the client that initiated the quit command.
     * @param worldGiven    The World object representing the game world.
     * @param robot         The Robot object representing the robot that initiated the quit command.
     */
    public void sendQuit (ClientHandler clientHandler, World worldGiven, Robot robot) {
        // broadcast to other clients
        for (ClientHandler cH : ClientHandler.clientHanders) {
            if (cH.getRobot() != null && cH != clientHandler) { // has launched robot into world.
                Response res = new StandardResponse(new HashMap<>(){{
                    put("message", "remove enemy");
                    put("robotName", robot.getName());
                }}, null);
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        clientHandler.setCurrentCommand(getName());
        ClientHandler.clientHanders.remove(clientHandler);

        Robot robot = clientHandler.getRobot();
        clientHandler.getWorld().getRobots().remove(robot);

        sendQuit(clientHandler, worldGiven, robot);

        return new BasicResponse("Successfully disconnected from server.");
    }
    
    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
