package client.userInterface.turtle;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import client.Client;
import client.robots.Robot;
import client.robots.util.State;
import client.userInterface.text.TextInterface;
import client.userInterface.turtle.util.Turtle;
import client.userInterface.util.Position;

/**
 * The TurtleInterface class represents the turtle-based user interface for the client.
 * It manages the display of the game world, including obstacles, player, and enemy players.
 */
public class TurtleInterface extends TextInterface implements Runnable {

    private Player player;
    private Robot robot;
    private State robotState;
    private Turtle pen;

    private List<Position> obstacles;
    private List<Player> enemyPlayers = new ArrayList<>();
    private List<String> enemyPlayersNames = new ArrayList<>();
    @SuppressWarnings("unused")
    private int visibility;

    private int obstacleSize = 0;
    int x_constraint = 2; 
    int y_constraint = 2; 

    private final int angle = 90;
    private final int scaleFactor = 20; // Scaling factor for enlarging points

    /**
     * Constructs a new TurtleInterface with the specified client.
     *
     * @param client The client associated with the interface.
     */
    public TurtleInterface(Client client) {
        super(client);
    }

    /**
     * Returns the player associated with the interface.
     *
     * @return The player associated with the interface.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Creates a new turtle object to serve as the pen.
     */
    public void createPen() {
        this.pen = new Turtle();
        this.pen.hide();
        this.pen.penColor("white");
        this.pen.shapeSize(scaleFactor, scaleFactor); // Scale the turtle shape size
    }

    /**
     * Displays the obstacles in the game world.
     */
    public void showObstacles() {
        displayBox(obstacleSize);
        displayBox(obstacleSize + 1 + obstacleSize);
        for (int i = 0; i < obstacles.size(); i++) {
            drawObstacle(obstacles.get(i));
        }
    }

    /**
     * Sets the obstacles in the game world.
     *
     * @param obstacles The list of obstacles.
     */
    public void setObstacles(List<Position> obstacles) {
        this.obstacles = obstacles;
    }

    /**
     * Sets the size in the game world.
     *
     * @param size The size.
     */
    public void setSizeWorld(int size) {
        this.x_constraint = size;
        this.y_constraint = size;
    }

    /**
     * Sets the size obstacles in the game world.
     *
     * @param size obstacles The list of obstacles.
     */
    public void setSizeOfObstacles(int obstacleSize) {
        this.obstacleSize = obstacleSize;
    }

    /**
     * Sets the visibility level of the interface.
     *
     * @param visibility The visibility level.
     */
    public void setVisibility(int visibilityGiven) {
        visibility = visibilityGiven;
    }

    /**
     * Draws an obstacle at the specified position using a predefined shape.
     *
     * @param obstacle The position of the obstacle.
     */
    public void drawObstacle(Position obstacle) {
        Turtle obstacleTurtle = new Turtle();
        obstacleTurtle.shape("square");
        obstacleTurtle.shapeSize(scaleFactor, scaleFactor); // Scale the obstacle size
        obstacleTurtle.penColor("pink"); // Set the color of the obstacle
        obstacleTurtle.up();
        obstacleTurtle.setPosition(obstacle.getX() * scaleFactor, obstacle.getY() * scaleFactor);
        obstacleTurtle.down();
        obstacleTurtle.show();
    }

    public void addEnemyPlayers() {
        for (Robot enemy : robot.getEnemyRobots()) {
            if (!enemyPlayersNames.contains(enemy.getName())) {
                Player potentialEnemy = new Enemy(enemy);
                enemyPlayers.add(potentialEnemy);
                enemyPlayersNames.add(enemy.getName());
            }
        }
    }

    /**
     * Adds new enemy players to the interface based on the client's robot.
     */
    public void removeEnemyPlayers() {
        Iterator<Player> iterator = enemyPlayers.iterator();
        while (iterator.hasNext()) {
            Player enemy = iterator.next();

            // Remove enemy robots who quit.
            if (!robot.getEnemyRobots().contains(enemy.getRobot())) {
                iterator.remove();
                enemyPlayersNames.remove(enemy.getRobot().getName());
                enemy.hide();
            }

            // Also remove robots who are killed.
            if (enemy.getRobot().getShields() < 0) {
                iterator.remove();
                enemyPlayersNames.remove(enemy.getRobot().getName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                enemy.hide();
            }
        }
    }

    /**
     * Returns the enemy player with the specified name.
     *
     * @param enemyName The name of the enemy player.
     * @return The enemy player with the specified name, or null if not found.
     */
    public Player getEnemyPlayer(String enemyName) {
        int enemyIndex = enemyPlayersNames.indexOf(enemyName);
        return enemyPlayers.get(enemyIndex);
    }

    /**
     * Draws a rectangle using the pen turtle.
     */
    public void drawRectangle() {
        this.pen.forward(this.obstacleSize * scaleFactor);
        this.pen.left(this.angle);
        this.pen.forward(this.obstacleSize * scaleFactor);
        this.pen.left(this.angle);
        this.pen.forward(this.obstacleSize * scaleFactor);
        this.pen.left(this.angle);
        this.pen.forward(this.obstacleSize * scaleFactor);
    }

    /**
     * Displays the box around the game world.
     */
    public void displayBox(int obstacleSize) {
        this.pen.up();
        this.pen.setPosition(-x_constraint * scaleFactor - obstacleSize * scaleFactor, y_constraint * scaleFactor + obstacleSize * scaleFactor);
        this.pen.setDirection(0.0);
        this.pen.down();
        this.pen.forward((x_constraint * 2 + obstacleSize * 2) * scaleFactor);
        this.pen.setDirection(270.0);
        this.pen.forward((y_constraint * 2 + obstacleSize * 2) * scaleFactor);
        this.pen.setDirection(180.0);
        this.pen.forward((x_constraint * 2 + obstacleSize * 2) * scaleFactor);
        this.pen.setDirection(90.0);
        this.pen.forward((y_constraint * 2 + obstacleSize * 2) * scaleFactor);
        this.pen.up();
    }

    /**
     * Draws a grid over the game world.
     */
    public void drawGrid() {
        this.pen.penColor("blue");
        this.pen.up();
        // Draw vertical lines
        for (int x = -x_constraint; x <= x_constraint; x++) {
            this.pen.setPosition(x * scaleFactor, y_constraint * scaleFactor);
            this.pen.setDirection(270.0);
            this.pen.down();
            this.pen.forward((y_constraint * 2) * scaleFactor);
            this.pen.up();
        }
        // Draw horizontal lines
        for (int y = -y_constraint; y <= y_constraint; y++) {
            this.pen.setPosition(-x_constraint * scaleFactor, y * scaleFactor);
            this.pen.setDirection(0.0);
            this.pen.down();
            this.pen.forward((x_constraint * 2) * scaleFactor);
            this.pen.up();
        }
    }

    @Override
    public void run() {
        try {
            Turtle.setCanvasSize(500, 500);
            Turtle.bgcolor(Color.black);
            Turtle.getWindow().setResizable(false);
        } catch (java.lang.ExceptionInInitializerError e) {
            return;
            // throw new RuntimeException("Unexpected error during GUI initialization.", e);
        } catch (java.awt.HeadlessException e) {
            return;
            // throw new RuntimeException("Unexpected error during GUI initialization.", e);
        }
        

        // Draw initial setup.
        createPen();
        drawGrid();
        showObstacles();

        player = new Player(client.getRobot(), false);
        player.show();
        robot = client.getRobot();

        while (!gameOver) {
            // Update player's position and direction.
            robotState = robot.getState();
            player.setDirection(robotState.getDirection());
            player.setPlayerPosition(robotState.getPosition()[0] * scaleFactor, robotState.getPosition()[1] * scaleFactor);

            // Show some state info in the title bar.
            Turtle.getWindow().setTitle(
                "[" + robotState.getPosition()[0] + "," + robotState.getPosition()[1] + "] " +
                robot.getName().toUpperCase() + " <" + robot.getKind() + ">" + " ".repeat(10) +
                "Health: " + robotState.getShields() + " ".repeat(10) +
                "Shots: " + robotState.getShots()
            );

            // Draw enemy players & remove any enemy player that quits or is killed.
            addEnemyPlayers();
            removeEnemyPlayers();

            // Update enemy players' state
            for (Player enemyPlayer : enemyPlayers) {
                State enemyState = enemyPlayer.getRobot().getState();
                enemyPlayer.setDirection(enemyState.getDirection());
                enemyPlayer.setPlayerPosition(enemyState.getPosition()[0] * scaleFactor, enemyState.getPosition()[1] * scaleFactor);
                enemyPlayer.getPlayer().show();
            }
        }
    }
}
