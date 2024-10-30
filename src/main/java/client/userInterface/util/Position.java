package client.userInterface.util;

/**
 * Represents a position in a two-dimensional space.
 */
public class Position {
    private final int x;
    private final int y;

    /**
     * Constructs a Position object with the specified coordinates.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the position.
     *
     * @return The x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the position.
     *
     * @return The y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Converts the position to an array of integers.
     *
     * @return An array containing the x and y coordinates in that order.
     */
    public int[] asArray() {
        return new int[] {x, y};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    /**
     * Checks if the current position is within a given rectangular region defined by the top-left and bottom-right positions.
     *
     * @param topLeft     The top-left position of the region.
     * @param bottomRight The bottom-right position of the region.
     * @return True if the current position is within the region, false otherwise.
     */
    public boolean isIn(Position topLeft, Position bottomRight) {
        boolean withinTop = this.y <= topLeft.getY();
        boolean withinBottom = this.y >= bottomRight.getY();
        boolean withinLeft = this.x >= topLeft.getX();
        boolean withinRight = this.x <= bottomRight.getX();
        return withinTop && withinBottom && withinLeft && withinRight;
    }

    /**
     * Returns a string representation of the position.
     *
     * @return A string representation of the position in the format [x,y].
     */
  
    @Override
    public String toString() {
        return "[" + getX() + "," + getY() + "] ";
    }
}
