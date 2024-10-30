package domain.commands;

import domain.response.ApiResponse;
import domain.response.Response;
import domain.world.World;
import java.util.HashMap;

/**
 * The Dump class provides a way to collect and return information about the current state of the game world.
 * It gathers details about obstacles, robots, and the world size, and encapsulates this information in a response object.
 */
public class Dump {
    private World world;

    /**
     * Constructs a Dump object with the specified world.
     * 
     * @param worldgiven the World object from which to gather information
     */
    public Dump(World worldgiven) {
        this.world = worldgiven;
    }

    /**
     * Executes the dump operation, collecting data from the world.
     * The collected data includes obstacles, robots, world size, and obstacle size.
     * Returns the data encapsulated in an ApiResponse object.
     *
     * @return a Response object containing the gathered information about the world
     */
    public Response execute() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("obstacles", world.getObstaclesAsPositions());
        data.put("robots", world.getRobots());
        data.put("worldSize", world.getSizeForDatabase());
        data.put("obstacleSize", world.getObstacleSize());
        return new ApiResponse(data);
    }
}
