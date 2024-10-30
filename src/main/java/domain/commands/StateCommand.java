package domain.commands;

import java.util.HashMap;

import domain.ClientController;
import domain.ClientHandler;
import domain.response.ApiResponse;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.world.Robot;
import domain.world.World;

public class StateCommand extends Command {

    /**
     * Constructs a StateCommand object.
     * Calls the superclass constructor to set the command name.
     */
    public StateCommand() {
        super("state");
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        Robot robot = clientHandler.getRobot();
        robot.setStatus("NORMAL");
        return new StandardResponse(new HashMap<>(){}, robot.getState(), "robot");
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        return new ApiResponse();
    }
}
