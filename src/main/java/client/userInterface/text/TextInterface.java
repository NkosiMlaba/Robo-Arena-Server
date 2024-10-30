package client.userInterface.text;

import com.fasterxml.jackson.databind.JsonNode;

import client.Client;
import client.commands.Command;
import client.json.JsonHandler;
import client.robots.Robot;
import client.userInterface.turtle.TurtleInterface;
import client.userInterface.util.Position;

import java.util.List;
import java.util.Scanner;

/**
 * The TextInterface class represents a text-based user interface for interacting with the client and robots.
 */
public class TextInterface {
    protected final Client client;
    private List<Position> obstacles;
    private int obstacleSize;
    private int sizeSide;
    protected boolean gameOver = false;
    private TurtleInterface gui;
    private Scanner scanner;

    /**
     * Constructs a new TextInterface with the specified client.
     *
     * @param client The client associated with the interface.
     */
    public TextInterface(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Retrieves the list of obstacles.
     *
     * @return The list of obstacles.
     */
    public List<Position> getObstacles() {
        return obstacles;
    }

    /**
     * Retrieves the TurtleInterface used by the TextInterface.
     *
     * @return The TurtleInterface used by the TextInterface.
     */
    public TurtleInterface getGui() {
        return gui;
    }

    /**
     * Retrieves user input from the scanner.
     *
     * @return The user input as a string.
     */
    public String getUserInput() {
        return scanner.nextLine();
    }

    /**
     * Prints a welcome message to the console, informing the user that they are connected to the server
     * and providing instructions on how to launch a robot into the world. It also displays an example
     * command and a list of available robot types.
     */
    public void printWelcome() {
        System.out.println("JHB_45> Connected to server. Type: 'launch <robootMake> <robotName> to launch robot into world.\n" +
                "Example: 'launch sniper mandisa'" + "\n" +
                " ".repeat(8) + "[sniper, venom, fighter]");
        System.out.println("JHB_45> What do you want to do?");
    }

    /**
     * Outputs the specified string.
     *
     * @param outputString The string to be output.
     */
    public void output(String outputString) {
        Robot robot = client.getRobot();
        JsonNode jsonStr = null;

        if (Command.currentCommand.equals("connect")) {
            handleConnect(outputString);
            printWelcome();
            return;
        }

        if (JsonHandler.isJsonString(outputString)) {
            jsonStr = JsonHandler.deserializeJsonTString(outputString);
        }

        if (outputString.contains("Shutting") || outputString.contains("disconnected.")) {
            handleDisconnection(outputString, robot);
        } else if (jsonStr != null && jsonStr.get("data").get("message") != null &&
                jsonStr.get("data").get("message").asText().contains("been shot")) {
            System.out.println(robot + "> You've been shot.");
        } else {
            handleCommandOutput(outputString, jsonStr, robot);
        }

        promptForNextCommand(robot);
    }

    /**
     * Handles the connection event by deserializing the JSON string and storing the obstacles, obstacle size, and world size.
     *
     * @param outputString The JSON string received from the server upon connection.
     */
    private void handleConnect(String outputString) {
        JsonNode jsonString = JsonHandler.deserializeJsonTString(outputString);
        JsonNode jsonStringSizes = JsonHandler.deserializeJsonTString(outputString);

        try {
            this.obstacles = JsonHandler.deserializeObstacles(jsonString.get("data").get("obstacles"));
            this.obstacleSize = jsonStringSizes.get("data").get("obstacleSize").asInt();
            this.sizeSide = jsonStringSizes.get("data").get("size").asInt();
        } catch (Exception e) {
        }
    }

    /**
     * Handles the disconnection event by printing a message to the console and setting the gameOver flag to true.
     *
     * @param outputString The message received from the server upon disconnection.
     * @param robot The robot associated with the disconnection event.
     */
    private void handleDisconnection(String outputString, Robot robot) {
        System.out.println(robot != null ? robot + " > " + outputString : "JHB_45" + "> " + outputString);
        gameOver = true;
    }

    /**
     * Handles the output from the server by parsing the command and calling the appropriate handler method.
     *
     * @param outputString The output received from the server.
     * @param jsonStr The JSON representation of the output string.
     * @param robot The robot associated with the output.
     */
    private void handleCommandOutput(String outputString, JsonNode jsonStr, Robot robot) {
        System.out.print(robot != null ? robot + "> " : "JHB_45" + "> ");

        switch (Command.currentCommand) {
            case "launch":
                handleLaunchCommand(outputString, jsonStr);
                break;

            case "state":
                handleStateCommand(outputString);
                break;

            case "look":
                handleLookCommand(outputString);
                break;

            case "forward":
            case "back":
                handleMoveCommand(outputString);
                break;

            case "turn":
                handleTurnCommand(outputString);
                break;

            case "repair":
                handleRepairCommand(outputString);
                break;

            case "reload":
                handleReloadCommand(outputString);
                break;

            case "fire":
                handleFireCommand(jsonStr);
                break;

            default:
                System.out.println(outputString);
                break;
        }
    }

    /**
     * Handles the launch command by checking if the output string is a JSON string and starting the GUI if necessary.
     *
     * @param outputString The output received from the server.
     * @param jsonStr The JSON representation of the output string.
     */
    private void handleLaunchCommand(String outputString, JsonNode jsonStr) {
        if (JsonHandler.isJsonString(outputString)) {
            int visibility = jsonStr.get("data").get("visibility").asInt();

            if (obstacles != null) {
                startGui(visibility);
            }

            System.out.println("robot has been launched");
        } else {
            System.out.println(outputString);
        }
    }

    /**
     * Handles the state command by deserializing the JSON string and printing the robot's shields, shots, and status.
     *
     * @param outputString The output received from the server.
     */
    private void handleStateCommand(String outputString) {
        JsonNode state = JsonHandler.deserializeJsonTString(outputString).get("state");
        int shields = state.get("shields").asInt();
        int shots = state.get("shots").asInt();
        String status = state.get("status").asText();

        System.out.println(shields + " shields, " + shots + " shots, status is " + status + ".");
    }

    /**
     * Handles the 'look' command by parsing the output string and printing the objects around the robot.
     *
     * @param outputString The output received from the server.
     */
    private void handleLookCommand(String outputString) {
        JsonNode data = JsonHandler.deserializeJsonTString(outputString).get("data");
        JsonNode objectsNode = data.get("objects");

        if (objectsNode != null) {
            System.out.println("Objects around you:");
            for (int i = 0; i < objectsNode.size(); i++) {
                System.out.println("- " + objectsNode.get(i).get("direction") + ", " +
                        objectsNode.get(i).get("distance") + " steps away from you. " +
                        "Type: " + objectsNode.get(i).get("type") + ".");
            }
        }
    }

    /**
     * Handles the 'forward' and 'back' commands by parsing the output string and printing the result of the move.
     *
     * @param outputString The output received from the server.
     */
    private void handleMoveCommand(String outputString) {
        JsonNode data = JsonHandler.deserializeJsonTString(outputString).get("data");
        String message = data.get("message").asText();
        if (message.equals("Done")) {
            System.out.println(Command.currentCommand.equals("forward") ? "moved forward!" : "moved back!");
        } else {
            System.out.println("You've been obstructed.");
        }
    }

    /**
     * Handles the 'turn' command by parsing the output string and printing the result of the turn.
     *
     * @param outputString The output received from the server.
     */
    private void handleTurnCommand(String outputString) {
        JsonNode data = JsonHandler.deserializeJsonTString(outputString).get("data");
        String message = data.get("message").asText();
        if (message.equals("Done")) {
            System.out.println("turned!");
        }
    }

    /**
     * Handles the 'repair' command by parsing the output string and printing the result of the repair.
     *
     * @param outputString The output received from the server.
     */
    private void handleRepairCommand(String outputString) {
        JsonNode data = JsonHandler.deserializeJsonTString(outputString).get("data");
        String message = data.get("message").asText();

        if (message.equals("Done")) {
            System.out.println("shields have been repaired.");
        }
    }

    /**
     * Handles the 'reload' command by parsing the output string and printing the result of the reload.
     *
     * @param outputString The output received from the server.
     */
    private void handleReloadCommand(String outputString) {
        JsonNode data = JsonHandler.deserializeJsonTString(outputString).get("data");
        String message = data.get("message").asText();

        if (message.equals("Done")) {
            System.out.println("shots have been reloaded.");
        }
    }

    /**
     * Handles the 'fire' command by parsing the output string and printing the result of the fire.
     *
     * @param jsonStr The JSON representation of the output string.
     */
    private void handleFireCommand(JsonNode jsonStr) {
        JsonNode data = jsonStr.get("data");
        String message = data.get("message").asText();

        if (message.equals("Hit")) {
            int distance = data.get("distance").asInt();
            String robotHit = data.get("robot").asText();

            if (gui != null && obstacles != null && gui.getPlayer() != null) {
                gui.getPlayer().fire(distance);
            }

            System.out.println("You shot " + robotHit + " " + distance
                    + " steps away from you. At position "
                    + data.get("state").get("position") + ".");
        } else {
            if (gui != null && obstacles != null && gui.getPlayer() != null) {
                gui.getPlayer().fire(Robot.bulletDistance);
            }
            System.out.println(message);
        }
    }

    /**
     * Prompts the user for the next command based on the current robot.
     *
     * @param robot The robot associated with the prompt.
     */
    private void promptForNextCommand(Robot robot) {
        System.out.println(robot != null ? robot + "> What should I do next?" : "JHB_45> What do you want to do?");
    }

    /**
     * Starts the GUI with the given parameters.
     *
     * @param visibility The visibility of the GUI.
     */
    private void startGui(int visibility) {
        gui = new TurtleInterface(client);
        Thread guiThread = new Thread(gui);

        gui.setObstacles(obstacles);
        gui.setVisibility(visibility);
        gui.setSizeWorld(sizeSide);
        gui.setSizeOfObstacles(obstacleSize);

        guiThread.start();
    }
}
