package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import database.WorldDatabaseManagerJDBC;
import domain.ClientHandler;
import domain.response.ErrorResponse;
import domain.world.Obstacle;
import domain.world.Robot;
import domain.world.World;
import json.JsonHandler;

import java.util.ArrayList;

/**
 * The ServerHandler class handles commands that come from the server.
 * It implements the Runnable interface to run as a separate thread and wait for
 * server commands from the console.
 */
public class ServerHandler implements Runnable {
    private Scanner scanner;
    private String command;
    private World world;
    private WorldDatabaseManagerJDBC manager = new WorldDatabaseManagerJDBC();

    /**
     * Constructs a new ServerHandler object.
     *
     * @param world the world instance
     */
    public ServerHandler(World world) {
        this.world = world;
    }

    /**
     * Runs the server handler, continuously listening for and processing user commands.
     * This method implements the Runnable interface and is designed to be run in a separate thread.
     * It creates a scanner to read input from the console and enters an infinite loop to process commands.
     * The following commands are supported:
     * - "dump": Displays information about the current state of the world
     * - "restore": Restores a previously saved world state
     * - "save": Saves the current world state
     * - "robots": Lists all robots in the world
     * - "quit": Terminates the server
     * - "clear": Clears the console screen
     * Any other input is treated as an unsupported command.
     */
    @Override
    public void run() {
        try {
            this.scanner = new Scanner(System.in);
            while(true) {
                command =  scanner.nextLine().toLowerCase();
                if (isQuitting(command)) {
                    break;
                };
                processCommand(command);
            }
        } catch (NoSuchElementException e) {
            System.err.println("No more lines to read from the input stream.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isQuitting (String quitString) {
        if (quitString != null && quitString.equalsIgnoreCase("quit")) {
            quit();
            return true;
        }
        return false;
    }



    public void processCommand (String command) {
        switch (command) {
            case "dump":
                dump();
                break;
            case "restore":
                restoreWorld();
                break;
            case "save":
                saveWorld();
                break;
            case "robots":
                robots();
                break;
            case "quit":
                quit();
                break;
            case "clear":
                clear();
                break;
            default:
                System.out.println("Unsupported command: " + command);
        }
    }

    public void setWorldDatabaseManagerJDBC(WorldDatabaseManagerJDBC WorldDatabaseManagerJDBC) {
        this.manager = WorldDatabaseManagerJDBC;
    }

    /**
     * Listens for input from the console and returns the input as a lowercase string.
     * This method will block until input is available, and will wait for 1 second if no input is available before checking again.
     * If the scanner is closed or the input is finished, this method will return without throwing an exception.
     *
     * @param scannerUsed the scanner to use for reading input
     * @return the input received from the console, as a lowercase string
     */
    public String listenForInput (Scanner scannerUsed) {
        while (true) {
            try {
                if (scannerUsed.hasNextLine()) {
                    String input = scannerUsed.nextLine().toLowerCase();
                    System.out.println("Received input: " + input);
                    return input;
                } else {
                    // No input available, wait for a while
                    System.out.println("Waiting for input...");
                    Thread.sleep(1000); // Wait for 1 second before checking again
                    continue;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                continue;
            } catch (NoSuchElementException e) {
                System.out.println("No line found, scanner is closed or input is finished");
                continue;
            }
        }
    }

    /**
     * Handles the quit command.
     * Sends a server disconnect response to all client handlers and shuts down the
     * server.
     */
    public void quit() {
        
        sendQuitToClients();

        output("Shutting down...");
        // System.exit(0);
    }

    /**
     * Sends a disconnect response to all connected client handlers, notifying them that the server has been shut down.
     */
    public void sendQuitToClients() {
        ErrorResponse response = new ErrorResponse("Server has been disconnected.");
        String responseJsonString = JsonHandler.serializeResponse(response);

        try {
            for (ClientHandler clientHandler : ClientHandler.clientHanders) {
                clientHandler.sendToClient(responseJsonString);
            }
        } catch (Exception e) {
        }
        
    }

    /**
     * Handles the robots command.
     * Prints the list of robots currently in the world.
     */
    public void robots() {
        if (world.getRobots().size() < 1) {
            output("There are currently no robots in this world.");
        }else{
            output(getRobotsString());
        }
    }

    /**
     * Returns a string representation of the robots in the world.
     *
     * @return a string representation of the robots
     */
    public String getRobotsString() {
        StringBuilder string = new StringBuilder();
        string.append("\nHere are the robots in this world:\n");
        for (Robot robot : world.getRobots()) {
            string.append("\t- " + robot + "\n");
        }
        return string.toString();
    }

    /**
     * Handles the dump command.
     * Prints information about the robots and obstacles in the world.
     */
    public void dump() {
        StringBuilder string = new StringBuilder();
        String robotsStr = world.getRobots().size() > 0 ? 
        getRobotsString() : "There are currently no robots in world.";

        string.append(robotsStr);
        string.append(("\nObstacles:\n"));

        List<Obstacle> obstacles = world.getObstacles();

        for (Obstacle obstacle: obstacles){
            string.append(obstacle.toString()).append("\n");
        }

        string.append(("\nTopLeft position: " + world.getTOP_LEFT().toString()));
        string.append(("\nBottomRight position: " + world.getBOTTOM_RIGHT().toString()+ "\n"));

        output(string.toString());
    } 

    /**
     * Clears the console screen.
     * This method uses ANSI escape codes to clear the screen and reset the cursor position.
     */
    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Outputs a string to the console with the server's IP address.
     *
     * @param string the string to output
     */
    public void output(String string) {
        try {
            System.out.println("SERVER <" + InetAddress.getLocalHost().getHostAddress() + "> : " + string);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restores a world from the database.
     * Retrieves the names of all saved worlds, prompts the user to select a world to restore,
     * and then loads the size and obstacles for the selected world from the database.
     * Finally, it sends a quit signal to all connected clients and updates the world with the restored data.
     */
    public void restoreWorld() {
        ArrayList<String> names = readNamesFromDatabase();
        
        if (names.isEmpty()) {
            output("No worlds found");
            return;
        }

        output(names.toString());
        String name = validateWorldExists(names);

        if (name.equals("quit")) {
            return;
        }
        
        int worldSize = getWorldSizeFromDatabase(name);
        if (worldSize == -1) {
            output("World size could not be retrieved from database");
            return;
        }

        ArrayList<ArrayList<Integer>> obstaclesInWorld = getObstaclesFromDatabase(name);
        sendQuitToClients();
        setNewParameters(worldSize, obstaclesInWorld);

        output("World '" + name + "' restored.");

    }

    

    /**
     * Retrieves the size of a specified world from the database.
     * This method queries the database to get the size of the world with the given name.
     * If an error occurs during the retrieval process, an error message is output to the console,
     * and -1 is returned to indicate an error.
     *
     * @param name the name of the world for which to retrieve the size
     * @return the size of the world, or -1 if an error occurs
     */
    public int getWorldSizeFromDatabase(String name) {
        try {
            return manager.retrieveWorldSize(name);
        } catch (Exception e) {
            output("Error restoring world");
            return -1;
        }
    }

    /**
     * Retrieves the obstacles for a specified world from the database.
     * This method queries the database to get a list of obstacles for the given world name.
     *
     * @param name the name of the world for which to retrieve obstacles
     * @return an ArrayList of ArrayLists of Integers representing the obstacles, 
     *         or an empty list if an error occurs
     */
    public ArrayList<ArrayList<Integer>> getObstaclesFromDatabase (String name) {
        try {
            return manager.retrieveWorldObstacles(name);
        } catch (SQLException e) {
            output("No obstacles found for this world");
            return new ArrayList<>();
        
        } catch (Exception e) {
            e.printStackTrace();
            output("Error restoring obstacles");
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves the names of all saved worlds from the database.
     * This method queries the database to get a list of all world names.
     * If an error occurs during the retrieval process, an error message is output to the console,
     * and an empty list is returned.
     *
     * @return an ArrayList of Strings containing the names of all saved worlds, or an empty list if an error occurs
     */
    public ArrayList<String> readNamesFromDatabase() {
        try {
            ArrayList<String>  names = manager.retrieveNamesOfWorlds();
            return names; 
        } catch (Exception e) {
            output("Error retrieving obstacles");
            return new ArrayList<>();
        }
    }

    /**
     * Saves the current world to the database after validating the world name and checking for existing entries.
     */
    public void saveWorld() {
        String name = validateName();
        
        if (name == "quit") {
            return;
        }

        Object[] worldExistsStatusArray = worldAlreadyExistsInDatabase (name);
        // if an error occured, abort the operation
        if (worldExistsStatusArray.length == 2) {
            return;
        }

        boolean worldExistsStatus= (boolean) worldExistsStatusArray[0];
        // if the world is not in database then save the world
        if (!worldExistsStatus) {
            saveWorld(name);
            return;
        }
        
        // since world already exists, does the user replace it? 
        if (!isReplacingWorld()) {
            output("Aborting save the world operation");
            return;
        }

        // update the world
        updateWorld(name);
    }

    /**
     * Saves the current world to the database with the specified name.

     * @param name the name to assign to the world being saved
     */
    public void saveWorld(String name) {
        manager.initialiseDatabase();
        world.setWorldName(name);
        
        try {
            manager.storeWorld(world);
        } catch (Exception e) {
            output("Error saving world");
            return;
        }
        output("World '" + name + "' saved successfully");
    }

    /**
     * Updates the size and obstacles of an existing world in the database.
     * This method uses the WorldDatabaseManagerJDBC to update the world size and obstacles 
     * for the specified world name in the database.
     *
     * @param name the name of the world to update
     */
    public void updateWorld(String name) {
        manager.updateWorldSize(name, world);
        manager.updateWorldObstacles(name, world);
        output("World '" + name + "' updated successfully");

    }

    /**
     * Checks if a world with the specified name already exists in the database.
     * This method queries the database to determine if a world with the given name exists.
     *
     * @param name the name of the world to check
     * @return an Object array where the first element is a boolean indicating existence (true if exists, false otherwise),
     *         and the second element (if present) is a string indicating an error
     */
    public Object[] worldAlreadyExistsInDatabase(String name) {
        try {
            boolean nameExists = manager.nameOfWorldExists(name);
            if (nameExists) {
                output("World " + name + " already exists");
                return new Object[]{true};

            } 
            return new Object[]{false};
        } 
        catch (Exception e) {
            e.printStackTrace();
            output("Error checking if world name exists.");
            return new Object[]{false, "Error"};
        }
    }

    /**
     * Prompts the user for input by displaying a specified message and reads the input from the console.
     * This method outputs the given prompt string to the console and waits for the user to enter input.
     * If an exception occurs while reading the input, an empty string is returned.
     *
     * @param promptString the message to display to the user
     * @return the input entered by the user, or an empty string if an exception occurs
     */
    public String prompForInput (String promptString) {
        output(promptString);
        try {
            String worldNameGiven = scanner.nextLine();
            return worldNameGiven;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Prompts the user to enter a valid world name and validates the input.
     * This method continuously asks the user to provide a world name until a valid
     * name is entered. The input is validated to ensure it is not null, empty, or "quit".
     * If the user enters "quit", the method will output a message and return "quit".
     *
     * @return the valid world name entered by the user, or "quit" if the user chooses to abort.
     */
    public String validateName () {
        while (true) {
            String worldNameGiven = prompForInput("Please enter a world name: ");
            if (worldNameGiven == null || worldNameGiven.length() == 0 || worldNameGiven.equals("")) {
                output("Please enter a valid world name.");
                continue;
            }

            if (worldNameGiven.equals("quit")) {
                output("Aborting world restore.");
                return "quit";
            }

            return worldNameGiven;
        }
    }

    /**
     * Prompts the user to confirm whether they want to replace an existing world.
     * This method repeatedly asks the user for input until a valid response is given.
     * 
     * @return true if the user confirms replacement of the existing world, false otherwise.
     */
    public boolean isReplacingWorld () {
        while (true) {
            String answer = prompForInput("Do you want to replace the existing world? (y/n)");
            if (answer == null || answer.length() == 0) {
                output("Please enter a valid response.");
                continue;
            }
            answer = answer.trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")){
                return true;
            }

            if (answer.equals("n") || answer.equals("no")){
                return false;
            }
        }
    }

    /**
     * Prompts the user to enter a valid world name from a list of available names.
     * This method repeatedly asks the user for input until a valid world name is entered, 
     * or the user chooses to quit the operation. The method performs several checks:
     * 
     * @param names an ArrayList of Strings containing the names of available worlds.
     * @return the valid world name entered by the user, or "quit" if the operation is aborted.
     */
    public String validateWorldExists (ArrayList<String> names) {
        while (true) {
            String worldNameGiven = prompForInput("Enter a world name to restore: ");
            
            if (worldNameGiven == null || worldNameGiven.length() == 0 || worldNameGiven.equals("")) {
                output("Please enter a valid world name.");
                output("These are the available worlds: " + names.toString());
                continue;
            }

            if (worldNameGiven.equals("quit")) {
                output("Aborting world restore.");
                return "quit";
            }

            if (names.contains(worldNameGiven)) {
                return worldNameGiven;
            }
        }
    }

    /**
     * Retrieves the current World instance associated with the ServerHandler.
     *
     * @return The current World instance.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Sets a new World instance to the ServerHandler.
     *
     * @param world The World instance to be set.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Retrieves the current WorldDatabaseManagerJDBC instance used by the ServerHandler.
     *
     * @return The current WorldDatabaseManagerJDBC instance.
     */
    public WorldDatabaseManagerJDBC getManager() {
        return manager;
    }

    /**
     * Sets a new WorldDatabaseManagerJDBC instance to the ServerHandler.
     *
     * @param manager The WorldDatabaseManagerJDBC instance to be set.
     */
    public void setManager(WorldDatabaseManagerJDBC manager) {
        this.manager = manager;
    }

    /**
     * Updates the World instance with new parameters, specifically the world size
     * and the obstacles in the world.
     *
     * @param worldSize The size of the world to be set.
     * @param obstaclesInWorld A list of obstacles to be set in the world.
     */
    public void setNewParameters(int worldSize, ArrayList<ArrayList<Integer>> obstaclesInWorld) {
        world.setSize(worldSize);
        world.setObstacles(obstaclesInWorld);
    }
}
