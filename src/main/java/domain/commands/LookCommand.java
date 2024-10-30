package domain.commands;

import java.util.*;

import domain.ClientHandler;
import domain.response.ErrorResponse;
import domain.response.Response;
import domain.response.StandardResponse;
import domain.world.*;
import domain.world.util.Position;
import domain.ClientController;


/**
 * Represents a command to look for obstacles and other robots in different directions from the current robot's position.
 * The command provides information about the objects in the robot's line of sight within the visibility range.
 */
public class LookCommand extends Command {
    String robotName = null;
    /**
     * Constructs a LookCommand object.
     */
    public LookCommand(String givenName) {
        super("look", givenName);
        robotName = givenName;
    }

    @Override
    public Response execute(ClientHandler clientHandler, World worldGiven) {
        domain.world.Robot robot = worldGiven.getRobotByName(robotName);
        HashMap<String,Object>  data = lookAround(worldGiven);
        return new StandardResponse(data, robot.getState(), "robot");
    }

    /**
     * Checks if an object is within the boundary of the robot's line of sight based on the direction.
     *
     * @param robotPos    The position of the robot.
     * @param objectPos   The position of the object to check.
     * @param direction   The direction of the line of sight.
     * @return True if the object is within the boundary, false otherwise.
     */
    public boolean inBoundary(Position robotPos, Position objectPos, String direction) {
        int size = World.getWorldConfiguration().getTileSize();
        int robotX = robotPos.getX() - size;
        int robbotY = robotPos.getY() - size;

        switch (direction) {
            case "NORTH":
            case "SOUTH":
                if (objectPos.getX() >= robotX && objectPos.getX() <= robotX + size || 
                    objectPos.getX() + size >= robotX && objectPos.getX() + size <= robotX + size) {
                    return true;
                }
                break;

            case "EAST":
            case "WEST":
                if (objectPos.getY() >= robbotY && objectPos.getY() < robbotY + size || 
                    objectPos.getY() + size >= robbotY && objectPos.getY() + size <= robbotY+ size) {
                    return true;
                } 
                break;
        }
        return false;
    }

    /**
     * Looks for obstacles and other robots in different directions from the current robot's position.
     * 
     * @param worldGiven The world in which the robot is located.
     * @return A HashMap containing the objects in the robot's line of sight.
     */
    public HashMap<String,Object> lookAround (World worldGiven) {
        domain.world.Robot robot = worldGiven.getRobotByName(robotName);
        
        if (robot == null) {
            return new HashMap<>() {{ put("objects", new ArrayList<>()); }};
        }

        List<Position> obstacles = worldGiven.getObstaclesAsPositions();
        List<Robot>  robots =  worldGiven.getRobots();
        int visibility = Integer.valueOf(World.getWorldConfiguration().getVisibility());
        int edge = worldGiven.getSizeForGUI(); //World.getWorldConfiguration().getXConstraint();

        List<Object> objects = new ArrayList<>();

        // NORTH
        // loop through obstacles
        for (Position obstaclePos : obstacles) {
            if (inBoundary(robot.getPosition(), obstaclePos, "NORTH")
                && robot.getDistanceY(obstaclePos) <= visibility
                && robot.getPosition().getY() < obstaclePos.getY()) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "NORTH");
                    put("type", "OBSTACLE");
                    put("distance", robot.getDistanceY(obstaclePos));
                }};
                objects.add(object);
            }
        }

        // loop through robots
        for (Robot otherRobot : robots) {
            if (inBoundary(robot.getPosition(), otherRobot.getPosition(), "NORTH")
                && robot.getDistance(otherRobot) <= visibility
                && robot.getPosition().getY() < otherRobot.getPosition().getY()
                && otherRobot != robot) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "NORTH");
                    put("type", "ROBOT");
                    put("distance", robot.getDistance(otherRobot));
                }};
                objects.add(object);
            }
        }

        // Check top edge
        if ((robot.getPosition().getY() + visibility) > edge) {
            Map<String, Object> object = new HashMap<>(){{
                put("direction", "NORTH");
                put("type", "EDGE");
                put("distance", edge - robot.getPosition().getY());
            }};
            objects.add(object);
        }

        // EAST
        // loop through obstacles
        for (Position obstaclePos : obstacles) {
            if (inBoundary(robot.getPosition(), obstaclePos, "EAST")
                    && robot.getDistanceX(obstaclePos) <= visibility
                    && robot.getPosition().getX() < obstaclePos.getX()) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "EAST");
                    put("type", "OBSTACLE");
                    put("distance", robot.getDistanceX(obstaclePos));
                }};
                objects.add(object);
            }
        }

        // loop through robots
        for (Robot otherRobot : robots) {
            if (inBoundary(robot.getPosition(), otherRobot.getPosition(), "EAST")
                    && robot.getDistance(otherRobot) <= visibility
                    && robot.getPosition().getX() < otherRobot.getPosition().getX()
                    && otherRobot != robot) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "EAST");
                    put("type", "ROBOT");
                    put("distance", robot.getDistance(otherRobot));
                }};
                objects.add(object);
            }
        }

        // Check right edge
        if ((robot.getPosition().getX() + visibility) > edge) {
            Map<String, Object> object = new HashMap<>(){{
                put("direction", "EAST");
                put("type", "EDGE");
                put("distance", edge - robot.getPosition().getX());
            }};
            objects.add(object);
        }

        // SOUTH
        // loop through obstacles
        for (Position obstaclePos : obstacles) {
            if (inBoundary(robot.getPosition(), obstaclePos, "SOUTH")
                    && robot.getDistanceY(obstaclePos) <= visibility
                    && robot.getPosition().getY() > obstaclePos.getY()) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "SOUTH");
                    put("type", "OBSTACLE");
                    put("distance", robot.getDistanceY(obstaclePos));
                }};
                objects.add(object);
            }
        }

        // loop through robots
        for (Robot otherRobot : robots) {
            if (inBoundary(robot.getPosition(), otherRobot.getPosition(), "SOUTH")
                    && robot.getDistance(otherRobot) <= visibility
                    && robot.getPosition().getY() > otherRobot.getPosition().getY()
                    && otherRobot != robot) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "SOUTH");
                    put("type", "ROBOT");
                    put("distance", robot.getDistance(otherRobot));
                }};
                objects.add(object);
            }
        }

        // Check bottom edge
        if ((robot.getPosition().getY() - visibility) < -edge) {
            Map<String, Object> object = new HashMap<>(){{
                put("direction", "SOUTH");
                put("type", "EDGE");
                put("distance", Math.abs(robot.getPosition().getY() + edge));
            }};
            objects.add(object);
        }


        // WEST
        // loop through obstacles
        for (Position obstaclePos : obstacles) {
            if (inBoundary(robot.getPosition(), obstaclePos, "WEST")
                    && robot.getDistanceX(obstaclePos) <= visibility
                    && robot.getPosition().getX() > obstaclePos.getX()) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "WEST");
                    put("type", "OBSTACLE");
                    put("distance", robot.getDistanceX(obstaclePos));
                }};
                objects.add(object);
            }
        }

        // loop through robots
        for (Robot otherRobot : robots) {
            if (inBoundary(robot.getPosition(), otherRobot.getPosition(), "WEST")
                    && robot.getDistance(otherRobot) <= visibility 
                    && robot.getPosition().getX() > otherRobot.getPosition().getX()
                    && otherRobot != robot) {
                Map<String, Object> object = new HashMap<>(){{
                    put("direction", "WEST");
                    put("type", "ROBOT");
                    put("distance", robot.getDistance(otherRobot));
                }};
                objects.add(object);
            }
        }

        // Check left edge
        if ((robot.getPosition().getX() - visibility) < -edge) {
            Map<String, Object> object = new HashMap<>(){{
                put("direction", "WEST");
                put("type", "EDGE");
                put("distance", Math.abs(robot.getPosition().getX() + edge));
            }};
            objects.add(object);
        }
        return new HashMap<>() {{ put("objects", objects); }};
    }

    @Override
    public Response execute(ClientController controller, World worldGiven) {
        domain.world.Robot robot = worldGiven.getRobotByName(robotName);
        if (robot == null || robot.getState() == null) {
            return new ErrorResponse("Robot does not exist in world");
        }
        
        HashMap<String,Object>  data = lookAround(worldGiven);
        return new StandardResponse(data, robot.getState(), "robot");
    }
}
