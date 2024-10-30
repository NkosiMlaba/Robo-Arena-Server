package domain.commands;

import com.fasterxml.jackson.databind.JsonNode;

import domain.ClientHandler;
import domain.response.Response;
import domain.world.World;
import json.JsonHandler;
import domain.ClientController;


/**
 * Represents a command in the game.
 * A command consists of a name and an optional argument.
 * Provides methods to retrieve the name and argument of the command.
 * Provides a static factory method to create a Command object based on a request string.
 * Subclasses of Command implement the execute() method to perform specific actions for the command.
 */
public abstract class Command {
    private final String name;
    private String argument;

    /**
     * Constructs a Command object with the specified name.
     * The name is trimmed and converted to lowercase.
     * The argument is initialized to an empty string.
     *
     * @param name the name of the command
     */
    public Command(String name) {
        this.name = name.trim().toLowerCase();
        this.argument = "";
    }

    /**
     * Constructs a Command object with the specified name and argument.
     * The name is passed to the other constructor, and the argument is trimmed.
     *
     * @param name the name of the command
     * @param argument the argument for the command
     */
    public Command(String name, String argument) {
        this(name);
        this.argument = argument.trim();
    }

    /**
     * Returns the name of the command.
     *
     * @return the name of the command
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the argument of the command.
     *
     * @return the argument of the command, or an empty string if none was provided
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Creates a Command object based on the provided request string.
     * Parses the request string into a JSON node and extracts the command, robot name, and arguments.
     * Returns a Command object corresponding to the command type.
     * @param request the request string containing the command information
     * @return a Command object based on the command type in the request string
     * @throws IllegalArgumentException if the command in the request is not supported
     */
    public static Command create(String request) {

        // deserialize the request string into a Json node and then extract the info you need.
        JsonNode requestJson;
        try {
            requestJson = JsonHandler.deserializeJsonTString(request);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported command: " + request);
        }
        
        String command;
        String robotName;
        JsonNode args;
        try {
            command = requestJson.get("command").asText();
            robotName = requestJson.get("robot").asText();
            args = requestJson.get("arguments");
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing commands in request");
        }

        switch (command){
            case "connect":
                return new ConnectCommand();
            case "launch":
                return new LaunchCommand(robotName, args);
            case "quit":
            case "off":
                return new QuitCommand();
            case "state":
                return new StateCommand();
            case "fire":
                return new FireCommand();
            case "look":
                return new LookCommand(robotName);
            case "repair":
                return new RepairCommand();
            case "reload":
                return new ReloadCommand();    
            case "forward":
                return new ForwardCommand(args.get(0).asText());
            case "back":
                return new BackCommand(args.get(0).asText());
            case "turn":
                return new TurnCommand(args.get(0).asText());
            default:
                throw new IllegalArgumentException("Unsupported command: " + command);
        }
    }

    /**
     * Executes the command for the given client handler.
     * Subclasses must implement this method to perform the specific actions for the command.
     * @param clientHandler the client handler executing the command
     * @return a Response object representing the result of executing the command
     */
    public abstract Response execute(ClientHandler clientHandler, World world);

    /**
     * Executes the command for the given client handler.
     * Subclasses must implement this method to perform the specific actions for the command.
     * @return a Response object representing the result of executing the command
     */
    public abstract Response execute (ClientController controller, World world);

    /**
     * Returns a string representation of the Command.
     * @return a string representation of the Command
     */
    @Override
    public String toString() {
        return this.getName() + " ";
    }
}
