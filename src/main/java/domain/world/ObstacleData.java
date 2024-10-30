package domain.world;

/**
 * This class represents the data of an obstacle in a world.
 */
public class ObstacleData {
    public int bottomLeftX = 0;
    public int bottomLeftY = 0;
    public int size_obstacle = 0;
    public String name_world = null;

    /**
     * Constructor to initialize an obstacle with given coordinates and size.
     *
     * @param x The x-coordinate of the bottom-left corner of the obstacle.
     * @param y The y-coordinate of the bottom-left corner of the obstacle.
     * @param givenSize The size of the obstacle.
     */
    public ObstacleData(int x, int y, int givenSize) {
        this.bottomLeftX = x;
        this.bottomLeftY = y;
        this.size_obstacle = givenSize;
    }

    /**
     * Default constructor to initialize an obstacle with default values.
     */
    public ObstacleData() {
    }

    /**
     * Getter for the name of the world.
     *
     * @return The name of the world.
     */
    public String getName() {
        return name_world;
    }

    /**
     * Setter for the name of the world.
     *
     * @param name The name of the world.
     */
    public void setWorldName(String name) {
        this.name_world = name;
    }

    /**
     * Getter for the x-coordinate of the bottom-left corner of the obstacle.
     *
     * @return The x-coordinate of the bottom-left corner of the obstacle.
     */
    public int getBottomLeftX() {
        return bottomLeftX;
    }

    /**
     * Setter for the x-coordinate of the bottom-left corner of the obstacle.
     *
     * @param x The x-coordinate of the bottom-left corner of the obstacle.
     */
    public void setBottomLeftX(int x) {
        this.bottomLeftX = x;
    }

    /**
     * Getter for the y-coordinate of the bottom-left corner of the obstacle.
     *
     * @return The y-coordinate of the bottom-left corner of the obstacle.
     */
    public int getBottomLeftY() {
        return bottomLeftY;
    }

    /**
     * Setter for the y-coordinate of the bottom-left corner of the obstacle.
     *
     * @param width The y-coordinate of the bottom-left corner of the obstacle.
     */
    public void setBottomLeftY(int width) {
        this.bottomLeftY = width;
    }

    /**
     * Getter for the size of the obstacle.
     *
     * @return The size of the obstacle.
     */
    public int getSize() {
        return size_obstacle;
    }

    /**
     * Setter for the size of the obstacle.
     *
     * @param height The size of the obstacle.
     */
    public void setSize(int height) {
        this.size_obstacle = height;
    }
}

