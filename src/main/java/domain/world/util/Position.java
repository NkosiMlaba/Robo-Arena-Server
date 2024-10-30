package domain.world.util;

/**
 * The Position class represents a position in a two-dimensional space.
 * It contains the x and y coordinates of the position.
 */
public class Position {
    private final int x;
    private final int y;

    /**
     * Constructs a new Position object with the specified x and y coordinates.
     *
     * @param x The x coordinate of the position.
     * @param y The y coordinate of the position.
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of the position.
     *
     * @return The x coordinate of the position.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the position.
     *
     * @return The y coordinate of the position.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the position as an array of two integers, where the first element is the x coordinate and the second element is the y coordinate.
     *
     * @return An array containing the x and y coordinates of the position.
     */
    public int[] asArray() {
        return new int[] {x, y};
    }

    /**
     * Checks if the position is within a specified rectangular area defined by two positions: the top-left and bottom-right corners.
     *
     * @param topLeft The position representing the top-left corner of the rectangular area.
     * @param bottomRight The position representing the bottom-right corner of the rectangular area.
     * @return True if the position is within the rectangular area, false otherwise.
     */
    public boolean isIn(Position topLeft, Position bottomRight) {
        boolean withinTop = this.y <= topLeft.getY();
        boolean withinBottom = this.y >= bottomRight.getY();
        boolean withinLeft = this.x >= topLeft.getX();
        boolean withinRight = this.x <= bottomRight.getX();
        return withinTop && withinBottom && withinLeft && withinRight;
    }

    /**
     * Returns a string representation of the position in the format [x, y].
     *
     * @return A string representation of the position.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public String toString() {
        return "[" + getX() + "," + getY() + "] ";
    }
}
