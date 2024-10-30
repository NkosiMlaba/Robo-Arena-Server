package domain.world;

/**
 * Defines an interface for obstacles you want to place in your world.
 */
public interface Obstacle {
    /**
     * Get X coordinate of bottom left corner of obstacle.
     * @return x coordinate
     */
    int getBottomLeftX();

    /**
     * Get Y coordinate of bottom left corner of obstacle.
     * @return y coordinate
     */
    int getBottomLeftY();

    /**
     * Gets the side of an obstacle (assuming square obstacles)
     * @return the length of one side in nr of steps
     */
    int getSize();
}
