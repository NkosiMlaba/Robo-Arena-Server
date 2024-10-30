package domain.commands;

import java.util.HashMap;

import domain.ClientHandler;
import domain.response.ApiResponse;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.world.World;
import domain.ClientController;

/**
 * Represents a "connect" command in the game.
 * Inherits from the Command class and provides the implementation for the connect command.
 * When executed, sets the current command in the client handler and returns a response indicating successful connection.
 */
public class ConnectCommand extends Command {
    public ConnectCommand() {
        super("connect");
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        clientHandler.setCurrentCommand(getName());
        World world = clientHandler.getWorld();
        return new StandardResponse(new HashMap<>(){{
            put("message", "connected"); 
            put("obstacles", world.getObstaclesAsPositions());
            put("size", world.getSizeForGUI());
            put("obstacleSize", world.getObstacleSize());

        }}, new HashMap<>(){});
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
