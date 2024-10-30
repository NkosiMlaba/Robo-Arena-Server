package domain.world;

import java.util.ArrayList;
import java.util.List;

import domain.world.util.Position;


public class SquareObstacle implements Obstacle {
    
    private Position position;
    public static List<Position> obstacles = new ArrayList<>();

    private static int size = World.getWorldConfiguration().getTileSize();

    /**
     * Creates a new SquareObstacle at the specified position.
     *
     * @param x the x-coordinate of the obstacle
     * @param y the y-coordinate of the obstacle
     */
    public SquareObstacle(int x, int y) {
        this.position = new Position(x, y);
    }

    public SquareObstacle(int x, int y, int givenSize) {
        this.position = new Position(x, y);
        // setSize(givenSize);
        
    }

    /**
     * Returns the x-coordinate of the bottom-left corner of the obstacle.
     *
     * @return the x-coordinate of the bottom-left corner
     */
    public int getBottomLeftX() {
        return this.position.getX();
    }

    /**
     * Returns the y-coordinate of the bottom-left corner of the obstacle.
     *
     * @return the y-coordinate of the bottom-left corner
     */
    public int getBottomLeftY() {
        return this.position.getY();
    }

    /**
     * Returns the size of the obstacle.
     *
     * @return the size of the obstacle
     */
    public int getSize() {
        return size;
    }

    public void setSize(int givenSize) {
        // this obviously needs to be changed , it does not work
        this.size = givenSize;
    }

    /**
     * Checks if the obstacle blocks the specified position.
     *
     * @param position the position to check
     * @param robot    the robot to ignore during the check
     * @return an array with the result of the check: [true] if the position is blocked by the obstacle,
     *         [true, otherRobot] if the position is blocked by another robot, or [false] if the position is not blocked
     */
    public static Object[] blocksPosition(Position position, Robot robot, World worldGiven) {
        int positionX = position.getX();
        int positionY = position.getY();

        // Check if the position is the same as the obstacle's position
        Object[] isBlockedObstacle = checkObstacles(positionX, positionY, worldGiven);
        if (((boolean) isBlockedObstacle[0]) == true) {
            return isBlockedObstacle;
        }

        Object[] isBlockedRobot = checkRobotsWorld(position, positionX, positionY, worldGiven, robot);
        if (((boolean) isBlockedRobot[0]) == true) {
            return isBlockedRobot;
        }

        return new Object[]{false};
    }

    /**
     * Checks if the specified position is blocked by an obstacle or a robot in the given world.
     *
     * @param position the position to check
     * @param worldGiven the world in which to check for obstacles and robots
     * @return an array with the result of the check:
     *         [true] if the position is blocked by an obstacle,
     *         [true, otherRobot] if the position is blocked by another robot,
     *         [false] if the position is not blocked
     */
    public static Object[] blocksPosition(Position position, World worldGiven) {
        int positionX = position.getX();
        int positionY = position.getY();

        Object[] isBlockedObstacle = checkObstacles(positionX, positionY, worldGiven);
        if (((boolean) isBlockedObstacle[0]) == true) {
            return isBlockedObstacle;
        }

        Object[] isBlockedRobot = checkRobotsLaunch(position, positionX, positionY, worldGiven);
        if (((boolean) isBlockedRobot[0]) == true) {
            return isBlockedRobot;
        }

        return new Object[]{false};
    }
    

    /**
     * Checks if this obstacle blocks the path that goes from coordinate (x1, y1) to (x2, y2).
     * Since our robot can only move in horizontal or vertical lines (no diagonals yet), we can assume that either x1==x2 or y1==y2.
     * @param a first position
     * @param b second position
     * @return `true` if this obstacle is in the way
     */
    public static Object[] blocksPath(Position a, Position b, Robot robot, World worldGiven) {
        if (a.getX() == b.getX()) { // y is changing
            return blocksYPath(a, b, robot, worldGiven);
        }
        else {
            return blocksXPath(a, b, robot, worldGiven);
        }
    }

    /**
     * Checks if the obstacle blocks the path from position a to position b.
     *
     * @param a     the starting position
     * @param b     the ending position
     * @param robot the robot to ignore during the check
     * @return an array with the result of the check: [true] if the path is blocked by the obstacle,
     *         [true, otherRobot] if the path is blocked by another robot, or [false] if the path is not blocked
     */
    public static Object[] blocksYPath(Position a, Position b, Robot robot, World worldGiven) {
        if ( b.getY() > a.getY()) { // moving up
            for (int i=a.getY(); i <= b.getY(); i++) {
                Object[] result = blocksPosition(new Position(a.getX(), i), robot, worldGiven);
                if ((boolean) result[0]) {
                    return result;
                }
            }
        }
        else{
            for (int i=a.getY(); i >= b.getY(); i--) {
                Object[] result = blocksPosition(new Position(a.getX(), i), robot, worldGiven);
                if ((boolean) result[0]) {
                    return result;
                }
            }
        }
        return new Object[]{false};
    }

    /**
     * Checks if the obstacle blocks the path from position a to position b along the x-axis.
     *
     * @param a     the starting position
     * @param b     the ending position
     * @param robot the robot to ignore during the check
     * @param worldGiven the world where the robot and obstacles are located
     * @return an array with the result of the check:
     *         [true] if the path is blocked by the obstacle,
     *         [true, otherRobot] if the path is blocked by another robot, or
     *         [false] if the path is not blocked
     */
    public static Object[] blocksXPath(Position a, Position b, Robot robot, World worldGiven) {
        if ( b.getX() > a.getX()) { // robot moving to the right
            for (int i=a.getX(); i <= b.getX(); i++) {
                Object[] result = blocksPosition(new Position(i, a.getY()), robot, worldGiven);
                if ((boolean) result[0]) {
                    return result;
                }
            }
        }
        else{
            for (int i=a.getX(); i >= b.getX(); i--) {
                Object[] result = blocksPosition(new Position(i, a.getY()), robot, worldGiven);
                if ((boolean) result[0]) {
                    return result;
                }
            }
        }
        return new Object[]{false};
    }

    /**
     * Returns a string representation of the SquareObstacle.
     *
     * @return a string representation of the SquareObstacle
     */
    @Override
    public String toString() {
        int endposX = getBottomLeftX() + size;
        int endposY = getBottomLeftY() + size;
        return "-At position (" + getBottomLeftX() + "," + getBottomLeftY() + ") to " + "(" +  endposX + "," + endposY+ ")";
    }

    /**
     * Checks if the specified position is blocked by another robot in the given world.
     *
     * @param position the position to check
     * @param positionX the x-coordinate of the position
     * @param positionY the y-coordinate of the position
     * @param worldGiven the world where the robot and obstacles are located
     * @param robot the robot to ignore during the check
     * @return an array with the result of the check:
     *         [true, otherRobot] if the position is blocked by another robot,
     *         [false] if the position is not blocked
     */
    public static Object[] checkRobotsWorld (Position position, int positionX, int positionY, World worldGiven, Robot robot) {
        // Check for other robots
        for (Robot otherRobot : worldGiven.getRobots()) {
            // Ignore the current robot
            if (otherRobot == robot) {
                continue;
            }

            // if the position is the same as the other robot's position
            if (positionX == otherRobot.getPosition().getX() && positionY == otherRobot.getPosition().getY()) {
                return new Object[]{true, otherRobot};
            }
        
            // Calculate the center coordinates of each square
            int otherRobotX = otherRobot.getPosition().getX();
            int otherRobotY = otherRobot.getPosition().getY();
            int robotPositionX = position.getX();
            int robotPositionY = position.getY();

            // Calculate the distance between the centers of the squares
            int distanceX = Math.abs(otherRobotX - robotPositionX);
            int distanceY = Math.abs(otherRobotY - robotPositionY);
        
            // Check if the current position is inside the other robot's boundary
            if (distanceX < size * 2 && distanceY < size * 2) {
                return new Object[]{true, otherRobot};
            }
        }

        return new Object[]{false};
    }

    /**
     * Checks if the specified position is blocked by an obstacle in the given world.
     *
     * @param positionX the x-coordinate of the position to check
     * @param positionY the y-coordinate of the position to check
     * @param worldGiven the world in which to check for obstacles
     * @return an array with the result of the check:
     *         [true] if the position is blocked by the obstacle,
     *         [false] if the position is not blocked
     */
    public static Object[] checkObstacles (int positionX, int positionY, World worldGiven) {
        // Check for obstacles
        for (Obstacle obstacle : worldGiven.getObstacles()) {
            
            // if the position is the same as the obstacle, return true
            if (positionX == obstacle.getBottomLeftX() && positionY == obstacle.getBottomLeftY()) {
                return new Object[]{true};
            }
            
            int obstacleRight = obstacle.getBottomLeftX() + obstacle.getSize();
            int obstacleTop = obstacle.getBottomLeftY() + obstacle.getSize() * 2;

            boolean isOverlapX = (obstacle.getBottomLeftX() <= positionX && positionX < obstacleRight + obstacle.getSize()) ||
                                (positionX <= obstacle.getBottomLeftX() && obstacle.getBottomLeftX() < positionX + obstacle.getSize());
            
            boolean isOverlapY = (obstacle.getBottomLeftY() <= positionY && positionY < obstacleTop) ||
                                (positionY <= obstacle.getBottomLeftY() && obstacle.getBottomLeftY() < positionY + obstacle.getSize());

            if (isOverlapX && isOverlapY) {
                return new Object[]{true};
            }
        }
        return new Object[]{false};
    }

    /**
     * Checks if the specified position is blocked by another robot in the given world.
     *
     * @param position the position to check
     * @param positionX the x-coordinate of the position
     * @param positionY the y-coordinate of the position
     * @param worldGiven the world in which to check for robots
     * @return an array with the result of the check:
     *         [true, otherRobot] if the position is blocked by another robot,
     *         [false] if the position is not blocked
     */
    public static Object[] checkRobotsLaunch (Position position, int positionX, int positionY, World worldGiven) {

        // Check for other robots
        for (Robot otherRobot : worldGiven.getRobots()) {

            // if the position is the same as the other robot's position
            if (positionX == otherRobot.getPosition().getX() && positionY == otherRobot.getPosition().getY()) {
                return new Object[]{true, otherRobot};
            }
        
            // Calculate the center coordinates of each square
            int otherRobotX = otherRobot.getPosition().getX();
            int otherRobotY = otherRobot.getPosition().getY();
            int robotPositionX = position.getX();
            int robotPositionY = position.getY();

            // Calculate the distance between the centers of the squares
            int distanceX = Math.abs(otherRobotX - robotPositionX);
            int distanceY = Math.abs(otherRobotY - robotPositionY);
        
            // Check if the current position is inside the other robot's boundary
            if (distanceX < size * 2 && distanceY < size * 2) {
                return new Object[]{true, otherRobot};
            }
        }

        return new Object[]{false};
    }
}
