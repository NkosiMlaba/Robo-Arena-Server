package domain;

import java.util.ArrayList;

import database.WorldDatabaseManagerJDBC;
import domain.world.World;
import domain.commands.Command;
import domain.commands.Dump;
import domain.response.*;
import json.JsonHandler;



/**
 * This class is responsible for controlling the client's interaction with the game world.
 * It handles commands, retrieves world data from the database, and manages the game world.
 */
public class ClientController {
    private World world;
    private WorldDatabaseManagerJDBC manager = new WorldDatabaseManagerJDBC();

    /**
     * Constructs a new ClientController with an empty world.
     */
    public ClientController() {
        world = new World();
    }

    /**
     * Handles a command by creating a new command object, executing it, and returning the response as a JSON string.
     *
     * @param request The command to be executed.
     * @return The response to the command as a JSON string.
     */
    public String handleCommand(String request) {
        
        
        try {
            Command newCommand = Command.create(request);
            Response response = newCommand.execute(this, world);
            String responseJsonString = JsonHandler.serializeResponse(response);
            return responseJsonString;
        } catch (IllegalArgumentException e) {
            Response response = new ErrorResponse("Missing commands in body of request");
            String responseJsonString = JsonHandler.serializeResponse(response);
            return responseJsonString;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Dumps the current state of the world as a JSON string.
     *
     * @return The current state of the world as a JSON string.
     */
    public String dumpWorld() {
        Dump worldDump = new Dump(world);
        Response worldStateString = worldDump.execute();
        return JsonHandler.serializeResponse(worldStateString);
    }

    /**
     * Fetches a world from the database by its name, populates the game world with its data,
     * and returns the world's state as a JSON string.
     *
     * @param worldName The name of the world to be fetched.
     * @return The state of the fetched world as a JSON string.
     */
    public String fetchWorldFromDatabase(String worldName) {
        try {
            ArrayList<String> names = manager.retrieveNamesOfWorlds();
            if (!names.contains(worldName)) {
                Response response = new BasicResponse("World not found in database");
                return JsonHandler.serializeResponse(response);
            }

            ArrayList<ArrayList<Integer>> obstacles = manager.retrieveWorldObstacles(worldName);
            int size = manager.retrieveWorldSize(worldName);
            if (size == -1) {
                Response response = new BasicResponse("World not found in database");
                return JsonHandler.serializeResponse(response);
            }

            world.setSize(size);
            world.setObstacles(obstacles);

            return dumpWorld();
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new BasicResponse("World not found in database");
            return JsonHandler.serializeResponse(response);
        }
    }
}
