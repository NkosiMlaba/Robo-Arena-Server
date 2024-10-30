package domain.world;

/**
 * This class represents the data of a world.
 */
public class WorldData {
    private String name;
    private int size;

    /**
     * Constructs a new WorldData object with the given name and size.
     *
     * @param name The name of the world.
     * @param givenSize The size of the world.
     */
    public WorldData(String name, int givenSize) {
        this.name = name;
        this.size = givenSize;
    }

    /**
     * Constructs a new WorldData object with default values.
     */
    public WorldData() {
    }

    /**
     * Returns the name of the world.
     *
     * @return The name of the world.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the world.
     *
     * @param name The new name of the world.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the size of the world.
     *
     * @return The size of the world.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size of the world.
     *
     * @param height The new size of the world.
     */
    public void setSize(int height) {
        this.size = height;
    }
}



