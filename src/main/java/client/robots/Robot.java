package client.robots;

import java.util.ArrayList;
import java.util.List;

import client.robots.util.State;

public class Robot {

    private String name;
    private int shields;
    private int shots;;
    private State state;
    private String kind;
    private List<Robot> enemies = new ArrayList<>();
    private List<String> enemyNames = new ArrayList<>();
    public static int bulletDistance = 100;

    // class variables, the same for all robots.
    private static int reload;
    private static int repair;
    private static int visibility;

    /**
     * Constructs a new Robot object with the specified name.
     *
     * @param name The name of the robot.
     */
    public Robot(String name) {
        this.name = name;
        this.shields = 0;
        this.shots = 10;
    }

    
    /**
     * Constructs a new Robot object with the specified name, kind, and state.
     *
     * @param name The name of the robot.
     * @param kind The kind (make) of the robot.
     * @param state The initial state of the robot.
     */
    public Robot(String name, String kind, State state) {
        this.name = name;
        this.kind = kind;
        this.state = state;
    }

    /**
     * Returns a list of all enemy robots currently in the same game.
     *
     * @return A list of enemy robots.
     */
    public List<Robot> getEnemyRobots() {
        return enemies;
    }

    /**
     * Adds a new enemy robot to the list of enemy robots.
     *
     * @param enemy The enemy robot to add.
     */
    public void addEnemy(Robot enemy) {
        enemies.add(enemy);
    }

    /**
     * Adds a new enemy's name to the list of enemy names.
     *
     * @param enemyName The name of the enemy robot to add.
     */
    public void addEnemyName(String enemyName) {
        enemyNames.add(enemyName);
    }

    /**
     * Removes an enemy robot from the list of enemy robots and enemy names.
     *
     * @param enemyName The name of the enemy robot to remove.
     */
    public void removeEnemy(String enemyName) {
        int enemyIndex = enemyNames.indexOf(enemyName);
        // Robot enemyRobot = enemies.get(enemyIndex);
        enemyNames.remove(enemyName);
        enemies.remove(enemyIndex);
    }

    /**
     * Updates the state of an enemy robot in the list of enemy robots.
     *
     * @param enemyName The name of the enemy robot to update.
     * @param enemyState The new state of the enemy robot.
     */
    public void updateEnemyState(String enemyName, State enemyState) {
        int enemyIndex = enemyNames.indexOf(enemyName);
        Robot enemyRobot = enemies.get(enemyIndex);
        enemyRobot.setState(enemyState);
    }

    /**
     * Returns the number of shields the robot has, i.e. Health.
     *
     * @return The shields value.
     */
    public int getShields() {
        return shields;
    }

    /**
     * Returns the shots of the robot.
     *
     * @return The shots value.
     */


    public int getShots() {
        return shots;
    }

    /**
     * Returns the name of the robot.
     *
     * @return The name value.
     */

    public String getName() {
        return name;
    }

    /**
     * Gets the visibility value shared by all robots.
     *
     * @return The visibility value.
     */
    public static int getVisibility() {
        return visibility;
    }

    /**
     * Sets the visibility value shared by all robots.
     *
     * @param visibility The visibility value to set.
     */

    public static void setVisibility(int visibility) {
        Robot.visibility = visibility;
    }

    /**
     * Returns the number of seconds it takes reload a robot.
     * Value shared by all robots.
     *
     * @return The reload value.
     */
    public static int getReload() {
        return reload;
    }

    /**
     * Sets the number of seconds it takes reload a robot.
     *
     * @param reload The reload value to set.
     */
    public static void setReload(int reload) {
        Robot.reload = reload;
    }

    /**
     * Returns the number of seconds it takes repair a robot.
     * Value shared by all robots.
     *
     * @return The repair value.
     */
    public static int getRepair() {
        return repair;
    }

    /**
     * Sets the number of seconds it takes repair a robot.
     *
     * @param reload The repair value to set.
     */
    public static void setRepair(int repair) {
        Robot.repair = repair;
    }

    /**
     * Sets the name of the robot.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the kind(make) of the robot.
     *
     * @return The kind value.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the kind(make) of the robot.
     *
     * @param kind The kind to set.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Sets the number of shields the robot has, i.e. Health
     *
     * @param shields The shields value to set.
     */
    public void setShields(int shields) {
        this.shields = shields;
    }

    /**
     * Sets the number of shots the robot has.
     *
     * @param shots The shots value to set.
     */
    public void setShots(int shots) {
        this.shots = shots;
    }

    /**
     * Sets the state of the robot.
     * Automatically update shots and shields as well.
     *
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
        // automatically update shots and shields as well.
        setShots(state.getShots());
        setShields(state.getShields());
    }

    /**
     * Gets the state of the robot.
     *
     * @return state of robot.
     */
    public State getState() {
        return state;
    }

    @Override
    public String toString() {
        return this.state.getPosition() != null?
                (
                "[" + this.state.getPosition()[0]+ "," + this.state.getPosition()[1] + "] "
                + this.name.toUpperCase() + " <" + this.kind + "> " + this.state.getDirection()
                )
                : 
                this.name;
    }
}