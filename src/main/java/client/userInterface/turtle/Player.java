package client.userInterface.turtle;

import java.awt.Color;

import client.robots.Robot;
import client.userInterface.turtle.util.Turtle;

public class Player {
    
    /**
    * The Player class represents a player in the turtle-based user interface.
    * It is responsible for managing the player's turtle, including movement, appearance, and firing bullets.
    */
    private Turtle player;
    private Robot robot;
    private final int angle = 90;
    private final int scaleFactor = 20;
    private final int constraint = 200;
    Turtle bullet;

    /**
     * Constructs a new Player with the specified robot and enemy status.
     *
     * @param robot    The robot associated with the player.
     * @param isEnemy  A boolean indicating whether the player is an enemy.
     */
    public Player(Robot robot, boolean isEnemy) {
        this.robot = robot;
        player = new Turtle();
        player.hide();
        player.shape("triangle");
        player.shapeSize(scaleFactor, scaleFactor);
        player.fillColor(getColor(robot.getKind(), isEnemy));
        player.outlineColor(getColor(robot.getKind(), isEnemy));
        player.setDirection(90);
        player.up();
        
        // Player's bullet
        bullet = new Turtle(player.getX(), player.getY());
        bullet.hide();
    }
    
    private Color getColor(String make, boolean isEnemy) {
        if (!isEnemy) {
            switch(make) {
                case "venom":
                    return Color.red;
                case "fighter":
                    return Color.green;
                default:
                    return Color.cyan;
            }
        }
        else {
            switch(make) {
                case "venom":
                    return new Color(150, 0, 0);
                case "fighter":
                    return new Color(0, 150, 0);
                default:
                    return new Color(0, 0, 150);
            }
        }
    }

    /**
     * Returns the player's turtle object.
     *
     * @return The player's turtle.
     */
    public Turtle getPlayer() {
        return player;
    }

    /**
     * Returns the robot associated with the player.
     *
     * @return The robot associated with the player.
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Hides the player's turtle.
     */
    public void hide() {
        player.hide();
    }

    /**
     * Shows the player's turtle.
     */
    public void show() {
        player.show();
    }

    /**
     * Moves the player's turtle forward by the specified number of steps.
     *
     * @param steps The number of steps to move forward.
     */
    public void moveForward(double steps) {
        player.forward(steps);
    }

    /**
     * Moves the player's turtle backward by the specified number of steps.
     *
     * @param steps The number of steps to move backward.
     */
    public void moveBack(double steps) {
        player.backward(steps);
    }

    /**
     * Turns the player's turtle to the right.
     */
    public void turnRight() {
        player.right(angle);
    }

    /**
     * Turns the player's turtle to the left.
     */
    public void turnLeft() {
        player.left(angle);
    }

    /**
     * Sets the position of the player's turtle to the specified coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public void setPlayerPosition(int x, int y) {
        player.setPosition(x, y);
    }

     /**
     * Sets the direction of the player's turtle based on the specified direction string.
     *
     * @param direction The direction string ("north", "east", "south", or "west").
     */
    public void setDirection(String direction) {
        switch(direction.toLowerCase()) {
            case "north" :
                player.setDirection(90);
                break;
            case "east":
                player.setDirection(0);
                break;
            case "south" :
                player.setDirection(270);
                break;
            case "west":
                player.setDirection(180);
                break;
        }
    }

    /**
     * Fires a bullet from the player's turtle with the specified number of steps.
     *
     * @param steps The number of steps the bullet should travel.
     */
    public void fire(int steps) {
        bullet.shape("circle");
        bullet.shapeSize(scaleFactor, scaleFactor);
        bullet.outlineColor("yellow");
        bullet.fillColor("yellow");
        bullet.setDirection(angle);
        bullet.up();
        bullet.setPosition(player.getX(), player.getY());
        bullet.setDirection(player.getDirection());
        bullet.show();

        // move bullet smoothly while it is still inside world & not exceeded bullet distance.
        int stepsToGo = steps;
        int bulletSpeed = 5;
        while (stepsToGo > 0 && Math.abs(bullet.getX()) <= constraint && Math.abs(bullet.getY()) <= constraint) {
            bullet.forward(bulletSpeed);
            stepsToGo-= 5;
        }
        bullet.hide();
    }
}
