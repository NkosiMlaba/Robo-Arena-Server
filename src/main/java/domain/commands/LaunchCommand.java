package domain.commands;

import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;

import domain.ClientHandler;
import domain.configuration.ConfigurationManager;
import domain.response.*;
import domain.world.Robot;
import domain.world.SquareObstacle;
import domain.world.World;
import domain.world.util.Position;
import json.JsonHandler;
import domain.ClientController;


public class LaunchCommand extends Command {

    private Robot robot;
    private String robotName;
    private String kind;
    private int shields;
    private int shots;

    /**
     * Constructs a LaunchCommand object with the specified robot name and arguments.
     *
     * @param robotName The name of the robot to launch.
     * @param args      The arguments containing the robot kind, shields, and shots.
     */
    public LaunchCommand(String robotName, JsonNode args) {
        super("launch");
        // Extract each element in the args array:
        this.robotName = robotName;
        this.kind = args.get(0).asText();
        int maxSheilds = new ConfigurationManager().getMaxSheilds();
        int robotSheilds = args.get(1).asInt();
        this.shields = robotSheilds > maxSheilds? maxSheilds : robotSheilds;
        this.shots = args.get(2).asInt();
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        if (!worldHasSpace(worldGiven)) {
            return new ErrorResponse("No more space in this world");
        }

        int size = worldGiven.getSizeForGUI();
        // System.out.println(size);

        Position start = getStartingPosition(size, worldGiven);

        // create robot.
        robot = new Robot(robotName, kind, shields, shots, clientHandler, size, start);

        // only add robot if it is not already in world.
        if (!worldGiven.robotInWorld(robot)) {
            worldGiven.addRobotToWorld(robot);
            // store robot into robot variable in clientHandler. this way each instance of ClientHandler is connected to a single instance of robot.
            clientHandler.setRobot(robot);
            sendCurrentRobotInfo(clientHandler);
            
            return new StandardResponse(clientHandler.getRobot().getData(), clientHandler.getRobot().getState(), "robot");
        }
        else {
            return new ErrorResponse("Too many of you in this world");
        }
    }

    /**
     * Generates a random integer within the specified range.
     * 
     * @param start The inclusive start of the range.
     * @param stop The inclusive end of the range.
     * @return A random integer between start and stop.
     */
    private int randomInt(int start, int stop) {
        Random random = new Random();
        return start + random.nextInt(stop - start + 1);
    }

    /**
     * Determines a starting position for the robot within the world.
     * 
     * @param size The size of the world.
     * @param worldGiven The world object representing the game world.
     * @return A valid starting position for the robot.
     */
    private Position getStartingPosition(int size, World worldGiven) {
        boolean positionBlocked = true;
        Position randomStartingPosition = new Position(0, 0);

        while (positionBlocked) {
            int randomX = randomInt(-size, size);
            int randomY = randomInt(-size, size);
            randomStartingPosition = new Position(randomX, randomY);
            positionBlocked = (boolean) SquareObstacle.blocksPosition(randomStartingPosition, worldGiven)[0];
        }
        return randomStartingPosition;
    }

    /**
     * Checks if there is space available in the world for more robots.
     * 
     * @param world The world object representing the game world.
     * @return True if the world has space for more robots, false otherwise.
     */
    public boolean worldHasSpace(World world) {
        int maxRobots = World.getWorldConfiguration().getMaxRobots();
        return world.getRobots().size() < maxRobots;
    }

    /**
     * Sends information about the newly launched robot to all clients.
     * 
     * @param clientHandler The client handler of the newly launched robot.
     */
    public void sendCurrentRobotInfo(ClientHandler clientHandler) {
        for (ClientHandler cH : ClientHandler.getClientHandlers()) {
            if (cH.getRobot() != null && cH != clientHandler) { // has launched robot into the world
                Response res = new StandardResponse(new HashMap<>(){{
                    put("message", "new robot launched into world");
                    put("robotName", robot.getName());
                    put("robotKind", robot.getKind());
                    put("robotState", robot.getState());
                }}, null, "gui");
                cH.sendToClient(JsonHandler.serializeResponse(res));
            }
        }
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        if (!worldHasSpace(worldGiven)) {
            return new ErrorResponse("No more space in this world");
        }

        int size = worldGiven.getSizeForGUI();
        // System.out.println(size);

        Position start = getStartingPosition(size, worldGiven);

        // create robot.
        robot = new Robot(robotName, kind, shields, shots, size, start);

        if (!worldGiven.robotInWorld(robot)) {
            worldGiven.addRobotToWorld(robot);
            return new StandardResponse(robot.getData(), robot.getState(), "robot");
        }
        else {
            return new ErrorResponse("Too many of you in this world");
        }
    }
}
