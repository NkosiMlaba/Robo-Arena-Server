package domain.configuration;

/**
 * This class manages the configuration settings for the application.
 */
public class ConfigurationManager {

    /**
     * Default constructor.
     */
    public ConfigurationManager() {
        
    }

    /**
     * Retrieves the port number from the configuration.
     *
     * @return the port number
     */
    public static int getPort() {
        return Config.PORT;
    }

    /**
     * Retrieves the visibility setting from the configuration.
     *
     * @return the visibility setting as a string
     */
    public static String getVisibility() {
        return String.valueOf(Config.VISIBILITY);
    }

    /**
     * Retrieves the reload setting from the configuration.
     *
     * @return the reload setting as a string
     */
    public static String getReload() {
        return String.valueOf(Config.RELOAD);
    }

    /**
     * Retrieves the repair setting from the configuration.
     *
     * @return the repair setting as a string
     */
    public static String getRepair() {
        return String.valueOf(Config.RELOAD);
    }

    /**
     * Retrieves the X constraint from the configuration.
     *
     * @return the X constraint
     */
    public static int getXConstraint() {
        return Config.XCONSTRAINT;
    }

    /**
     * Retrieves the Y constraint from the configuration.
     *
     * @return the Y constraint
     */
    public static int getYConstraint() {
        return Config.YCONSTRAINT;
    }

    /**
     * Retrieves the maximum number of shields from the configuration.
     *
     * @return the maximum number of shields
     */
    public static int getMaxSheilds() {
        return Config.MAX_SHIELDS;
    }

    /**
     * Retrieves the tile size from the configuration.
     *
     * @return the tile size
     */
    public static int getTileSize() {
        return Config.TILE_SIZE;
    }

    /**
     * Retrieves the maximum number of robots from the configuration.
     *
     * @return the maximum number of robots
     */
    public static int getMaxRobots() {
        return Config.MAX_ROBOTS;
    }
    
    /**
     * Sets the X and Y constraints in the configuration to half of the given size.
     *
     * @param size the size to set the constraints to
     */
    public static void setSize (int size) {
        Config.YCONSTRAINT = size / 2;
        Config.XCONSTRAINT = size / 2;
    }
}
