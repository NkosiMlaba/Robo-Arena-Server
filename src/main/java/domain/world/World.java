package domain.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import domain.configuration.ConfigurationManager;
import domain.world.util.Position;
import domain.world.util.UpdateResponse;

/**
* Enum used to track direction
*/
enum Direction {
    NORTH, EAST, SOUTH, WEST
}

/**
 * The World class represents the game world, including its configuration, obstacles, and robots.
 */
public class World {
    public static  ConfigurationManager worldConfiguration = new ConfigurationManager();
    protected Position TOP_LEFT = new Position(-worldConfiguration.getXConstraint(), worldConfiguration.getYConstraint());
    protected Position BOTTOM_RIGHT = new Position(worldConfiguration.getXConstraint(), -worldConfiguration.getYConstraint());
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Position> obstaclesAsPositions = new ArrayList<>();
    private HashMap<String, Robot> nameAndRobotMap = new HashMap<String, Robot>();
    public static ArrayList<Robot> robots;
    public String worldName = "";
    
    /**
     * Constructs a new World object.
     * Initializes the list of robots and creates obstacles in the world.
     */
    public World(){
        robots = new ArrayList<>();
        this.obstacles = createObstacles();
    }

    /**
     * Constructs a new World object.
     * Initializes the list of robots and creates obstacles in the world.
     */
    public World(int size) {
        robots = new ArrayList<>();
        setSize(size);
    }

    /**
     * Constructs a new World object.
     * Initializes the list of robots and creates obstacles in the world.
     */
    public World(int size, List<Obstacle> obstaclesGiven) {
        robots = new ArrayList<>();
        setSize(size);
        setObstacles(obstaclesGiven);
    }

    

    /**
     * Returns the top-left corner position of the world.
     *
     * @return the top-left corner position of the world.
     */
    public Position getTOP_LEFT () {
        return TOP_LEFT;
    }

    /**
     * Returns the bottom-right corner position of the world.
     *
     * @return the bottom-right corner position of the world.
     */
    public Position getBOTTOM_RIGHT() {
        return BOTTOM_RIGHT;
    }

    /**
     * Returns the current world object.
     *
     * @return the current world object.
     */
    public World getWorld() {
        return this;
    } 

    /**
     * Sets the obstacles in the world using a list of Obstacle objects.
     *
     * @param obstaclesGiven a list of Obstacle objects representing the obstacles to be added to the world.
     *                        Only obstacles within the world boundaries will be added.
     */
    public void setObstacles(List<Obstacle> obstaclesGiven) {
        
        for (Obstacle obstacle : obstaclesGiven) {
            Position obstaclePosition = new Position(obstacle.getBottomLeftX(), obstacle.getBottomLeftY());
            if (obstaclePosition.isIn(TOP_LEFT, BOTTOM_RIGHT)) {
                this.obstacles.add(obstacle);
                this.obstaclesAsPositions.add(obstaclePosition);
            }
        }
    }

    /**
     * Sets the obstacles in the world using a list of ArrayList<Integer> objects.
     *
     * @param obstaclesGiven a list of ArrayList<Integer> objects representing the obstacles to be added to the world.
     *                        Each ArrayList contains the x-coordinate, y-coordinate, and size of the obstacle.
     *                        Only obstacles within the world boundaries will be added.
     */
    public void setObstacles(ArrayList<ArrayList<Integer>> obstaclesGiven) {
        
        this.obstacles = new ArrayList<>();
        this.obstaclesAsPositions = new ArrayList<>();
        for (ArrayList<Integer> obstacleArrayList : obstaclesGiven) {
            Position obstaclePosition = new Position(obstacleArrayList.get(0), obstacleArrayList.get(1));
            if (obstaclePosition.isIn(TOP_LEFT, BOTTOM_RIGHT)) {
                this.obstacles.add(new SquareObstacle(obstacleArrayList.get(0), obstacleArrayList.get(1), obstacleArrayList.get(2)));
                this.obstaclesAsPositions.add(obstaclePosition);
            }
        }
    }

    /**
     * Sets the size of the world.
     *
     * @param eachSide the side length of the square world.
     *                 The top-left corner will be at (-eachSide/2, eachSide/2) and the bottom-right corner will be at (eachSide/2, -eachSide/2).
     */
    public void setSize(int eachSide) {
        TOP_LEFT = new Position(-(eachSide / 2), eachSide / 2);
        BOTTOM_RIGHT = new Position(eachSide / 2, - (eachSide / 2));
    }

    /**
     * Generates a random integer within the specified range.
     *
     * @param start the start of the range (inclusive).
     * @param stop the end of the range (exclusive).
     * @return a random integer within the specified range.
     */
    private int randomInt(int start, int stop) {
        Random random = new Random();
        return start + random.nextInt(stop - start + 1);
    }

    /**
     * Returns the world configuration.
     *
     * @return the ConfigurationManager object representing the world configuration
     */
    public static ConfigurationManager getWorldConfiguration() {
        return worldConfiguration;
    }

    /**
     * Creates a list of obstacles in the world.
     *
     * @return a list of obstacles
     */
    public List<Obstacle> createObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        int numberOfObstacles = BOTTOM_RIGHT.getX() / 2;
        for (int i = 0; i < numberOfObstacles ; i++) {
            int x = randomInt(TOP_LEFT.getX(), BOTTOM_RIGHT.getX());
            int y = randomInt(BOTTOM_RIGHT.getY(), TOP_LEFT.getY());
            // rather check if the position is blocked.
            if (!SquareObstacle.obstacles.contains(new Position(x, y))) {
                SquareObstacle obstacle = new SquareObstacle(x, y);
                obstacles.add(obstacle);
                obstaclesAsPositions.add(new Position(x, y));
            }
        }
        return obstacles;
    }

    /**
     * Creates a new square obstacle with the given coordinates.
     *
     * @param x the x-coordinate of the bottom-left corner of the square obstacle
     * @param y the y-coordinate of the bottom-left corner of the square obstacle
     *
     * @return a new SquareObstacle object with the given coordinates
     */
    public Obstacle createOneObstacle (int x, int y) {
        return new SquareObstacle(x, y);
    }

    /**
     * Returns the list of obstacles in the world.
     *
     * @return the list of obstacles
     */
    public List<Obstacle> getObstacles() {
        return this.obstacles;
    }

    /**
     * Displays the obstacles in the world.
     * Prints the position of each obstacle to the console.
     */
    public void showObstacles() {
        List<Obstacle> obstacles = getObstacles();
        if (!obstacles.isEmpty()) {
            System.out.println("There are some obstacles:");
            for (int i=0; i < obstacles.size(); i++) {
                System.out.println(obstacles.get(i));
            }
        }
    }

    /**
     * Returns the list of robots in the world.
     *
     * @return the list of robots
     */
    public ArrayList<Robot> getRobots() {
        return robots;
    }

    /**
     * Adds a robot to the world.
     *
     * @param robot the robot to add
     */
    public void addRobotToWorld(Robot robot) {
        robots.add(robot);
        nameAndRobotMap.put(robot.getName(), robot);
    }
    

    /**
     * Removes a robot from the world.
     *
     * @param robot the robot to remove
     */
    public void removeRobot(Robot robot) {
        robots.remove(robot);
    }

    /**
     * Checks if a robot is in the world.
     *
     * @param robot the robot to check
     * @return true if the robot is in the world, false otherwise
     */
    public boolean robotInWorld(Robot robot) {
        for (Robot robotInWorld : robots) {
            if (robot.getName().equalsIgnoreCase(robotInWorld.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the position of a robot in the world.
     *
     * @param robot    the robot to update
     * @param nrSteps  the number of steps to move the robot
     * @param isBullet indicates if the update is for a bullet (true) or robot movement (false)
     * @return an array containing the update response, which can be UpdateResponse.SUCCESS, UpdateResponse.FAILED_OBSTRUCTED,
     * or UpdateResponse.FAILED_OUTSIDE_WORLD, and additional information if applicable
     */
    public Object[] updatePosition(Robot robot, int nrSteps) { // remove isBullet from this method

        int increment = (nrSteps > 0)? 1 : -1;
        Object[] updateResponse = null;

        // move robot by 1 / -1 step until it reaches its destination or it is obstructed / out of safezone
        for (int i=1; i<=Math.abs(nrSteps); i++) {
            updateResponse = updatePosition_helper(robot, increment, false); 
            if (updateResponse[0] != UpdateResponse.SUCCESS) { //obstructed or out of world.
                return updateResponse;
            }
        }

        return updateResponse;
    };

    /**
     * Fires a bullet from the given robot in the current direction.
     * The bullet will move for the specified number of steps.
     * If the bullet hits an obstacle or another robot, it will stop.
     *
     * @param robot    the robot that fires the bullet
     * @param nrSteps  the number of steps the bullet will move
     * @return an array containing the update response, which can be UpdateResponse.SUCCESS or UpdateResponse.FAILED_OBSTRUCTED,
     * and additional information if another robot obstructs the path
     */
    public Object[] fireGun(Robot robot, int nrSteps) {
        return updatePosition_helper(robot, nrSteps, true); 
    }

    /**
     * Helper method to update the position of a robot in the world.
     *
     * @param robot    the robot to update
     * @param nrSteps  the number of steps to move the robot
     * @param isBullet indicates if the update is for a bullet (true) or robot movement (false)
     * @return an array containing the update response, which can be UpdateResponse.SUCCESS or UpdateResponse.FAILED_OBSTRUCTED,
     * and additional information if another robot obstructs the path
     */
    Object[] updatePosition_helper(Robot robot, int nrSteps, Boolean isBullet) {
        int newX = robot.getPosition().getX();
        int newY = robot.getPosition().getY();

        if (Direction.NORTH.equals(robot.getDirection())) {
            newY = newY + nrSteps;
        }
        else if (Direction.EAST.equals(robot.getDirection())) {
            newX = newX + nrSteps;
        }
        else if (Direction.SOUTH.equals(robot.getDirection())) {
            newY = newY - nrSteps;
        }
        else if (Direction.WEST.equals(robot.getDirection()) ) {
            newX = newX - nrSteps;
        }

        Position newPosition = new Position(newX, newY);

        Object[] result = SquareObstacle.blocksPath(robot.getPosition(), newPosition, robot, this);

        if ((boolean) result[0]) { // path is blocked, either by an obstacle (result.length == 1) || by other robot (result.length == 2).
            if (result.length == 1) {
                return new Object[]{UpdateResponse.FAILED_OBSTRUCTED};
            }else{
                return new Object[]{UpdateResponse.FAILED_OBSTRUCTED, result[1]}; // also return the otherRobot
            }
        }
        else if (newPosition.isIn(this.TOP_LEFT,this.BOTTOM_RIGHT)){ 
            if (!isBullet) {
                robot.setPosition(newPosition);
            }
            return new Object[]{UpdateResponse.SUCCESS};
        }

        return new Object[]{UpdateResponse.FAILED_OUTSIDE_WORLD};
    };

    /**
     * Updates the direction of a robot in the world.
     *
     * @param robot     the robot to update
     * @param turnRight indicates whether to turn the robot right (true) or left (false)
     */
    public void updateDirection(Robot robot, boolean turnRight){
        
        if (turnRight) {
            Right(robot, true);
        } else {
            Left(robot, true);
        }
    }

    /**
     * Rotates the robot's direction to the left.
     *
     * @param robot the robot to rotate
     * @param turnLeft a boolean indicating whether to turn the robot left (true) or not (false).
     *                 This parameter is not used in this method, but is included for consistency with the Right method.
     */
    public void Left(Robot robot, boolean turnLeft){
        if (turnLeft) {
            switch (String.valueOf(robot.getDirection())) {
                case "NORTH":
                    robot.setDirection(Direction.WEST);
                    break;
                case "EAST":
                    robot.setDirection(Direction.NORTH);
                    break;
                case "SOUTH":
                    robot.setDirection(Direction.EAST);
                    break;
                case "WEST":
                    robot.setDirection(Direction.SOUTH);
                    break;
            }
        }
    }

    /**
     * Rotates the robot's direction to the right.
     *
     * @param robot the robot to rotate
     * @param turnRight a boolean indicating whether to turn the robot right (true) or not (false).
     *                  This parameter is not used in this method, but is included for consistency with the Left method.
     */
    public void Right(Robot robot, boolean turnRight){
        if (turnRight) {
            switch (String.valueOf(robot.getDirection())) {
                case "NORTH":
                    robot.setDirection(Direction.EAST);
                    break;
                case "EAST":
                    robot.setDirection(Direction.SOUTH);
                    break;
                case "SOUTH":
                    robot.setDirection(Direction.WEST);
                    break;
                case "WEST":
                    robot.setDirection(Direction.NORTH);
                    break;
            }
        }
    }

    /**
     * Returns the size of the world for the GUI.
     *
     * @return the size of the world for the GUI, which is the x-coordinate of the bottom-right corner of the world.
     */
    public int getSizeForGUI () {
        int x = getBOTTOM_RIGHT().getX();
        return x; // fine like this for now because size is square
    }

    /**
     * Returns the size of the world for the database.
     *
     * @return the size of the world for the database, which is twice the x-coordinate of the bottom-right corner of the world.
     */
    public int getSizeForDatabase () {
        int x = getBOTTOM_RIGHT().getX() * 2;
        return x; // fine like this for now because size is square
    }

    /**
     * Returns the size of an obstacle.
     *
     * @return the size of an obstacle, which is the side length of the square obstacle.
     */
    public int getObstacleSize () {
        SquareObstacle obstacle = new SquareObstacle(5, 5);
        int x = obstacle.getSize();
        return x; // fine like this for now because size is square
    }

    /**
     * Returns the list of obstacle positions in the world.
     *
     * @return a list of Position objects representing the positions of the obstacles in the world.
     */
    public List<Position> getObstaclesAsPositions () {
        return obstaclesAsPositions;
    }

    /**
     * Returns the name of the world.
     *
     * @return the name of the world.
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the name of the world.
     *
     * @param nameGiven the new name for the world.
     */
    public void setWorldName(String nameGiven) {
        this.worldName = nameGiven;
    }

    /**
     * Adds an obstacle to the world.
     *
     * @param obstacle the obstacle to add to the world.
     */
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
        obstaclesAsPositions.add(new Position(obstacle.getBottomLeftX(), obstacle.getBottomLeftY()));
    }

    /**
     * Returns a robot with the given name from the world.
     *
     * @param nameOfRobot the name of the robot to search for.
     * @return the robot with the given name, or null if no robot with the given name is found.
     */
    public Robot getRobotByName (String nameOfRobot) {
        for (String key: nameAndRobotMap.keySet()) {
            if (nameOfRobot.equals(key)) {
                return nameAndRobotMap.get(key);
            }
        }
        return null;
    }

}
