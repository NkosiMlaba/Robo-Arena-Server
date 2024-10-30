package client.userInterface.turtle;

import client.robots.Robot;

/**
 * The Enemy class represents an enemy player in the turtle-based user interface.
 * It extends the Player class and represents an enemy controlled by the game.
 */
public class Enemy extends Player{

    /**
     * Constructs a new Enemy with the specified robot.
     *
     * @param robot The robot associated with the enemy.
     */
    public Enemy(Robot robot) {
        super(robot, true);
    }
    
}
