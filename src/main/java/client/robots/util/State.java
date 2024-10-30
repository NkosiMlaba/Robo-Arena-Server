package client.robots.util;

import java.util.Arrays;

public class State {
    private int[] position;
    private String direction;
    private int shields;
    private int shots;
    private String status;

    /**
     * Returns the current position of the robot.
     * @return an array of two integers representing the x and y coordinates of the robot's position.
     */
    public int[] getPosition() { return position; }

    /**
     * Sets the position of the robot.
     * @param position an array of two integers representing the x and y coordinates of the robot's new position.
     */
    public void setPosition(int[] position) { this.position = position; }

    /**
     * Returns the current direction of the robot.
     * @return a string representing the direction of the robot.
     */
    public String getDirection() { return direction; }

    /**
     * Sets the direction of the robot.
     * @param direction a string representing the new direction of the robot.
     */
    public void setDirection(String direction) { this.direction = direction; }

    /**
     * Returns the current number of shields the robot has.
     * @return an integer representing the number of shields.
     */
    public int getShields() { return shields; }

    /**
     * Sets the number of shields the robot has.
     * @param shields an integer representing the new number of shields.
     */
    public void setShields(int shields) { this.shields = shields; }

    /**
     * Returns the current number of shots the robot has.
     * @return an integer representing the number of shots.
     */
    public int getShots() { return shots; }

    /**
     * Sets the number of shots the robot has.
     * @param shots an integer representing the new number of shots.
     */
    public void setShots(int shots) { this.shots = shots; }

    /**
     * Returns the current status of the robot.
     * @return a string representing the status of the robot.
     */
    public String getStatus() { return status; }
    
    /**
     * Sets the status of the robot.
     * @param status a string representing the new status of the robot.
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns a string representation of the robot's state.
     * @return a string in the format "State(position: [x, y], direction: ..., shots: ..., shields: ..., status: ...)".
     */
    @Override
    public String toString() {
        return "State(positon: "+ Arrays.toString(position) + " direction: "  + direction 
                + " shots: " + shots + " shields: " + shields +  " status: " + status + ")";
    }
}
