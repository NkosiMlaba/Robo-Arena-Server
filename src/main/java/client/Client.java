package client;

import client.commands.Command;
import client.commands.LaunchCommand;
import client.commands.QuitCommand;
import client.commands.ReloadCommand;
import client.commands.RepairCommand;
import client.request.Request;
import client.json.JsonHandler;
import client.robots.Fighter;
import client.robots.Robot;
import client.robots.Sniper;
import client.robots.Venom;
import client.robots.util.State;
import client.userInterface.text.TextInterface;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private Robot robot;
    private boolean paused;
    private static Command currentCommand;

    private OutputStream outputStream;
    private InputStream inputStream;
    private PrintStream outToServer;

    private TextInterface textInterface;

    /**
    * Constructs a new Client object with the specified socket.
    *
    * @param socket The socket used for communication with the server.
    */
    public Client(Socket socket) {
        try {
            this.socket = socket;
            this.outputStream = socket.getOutputStream();
            this.inputStream = socket.getInputStream();
            outToServer = new PrintStream(socket.getOutputStream());
            this.paused = false;
            this.textInterface = new TextInterface(this);
        } catch (IOException e) {
            closeEverything(socket, inputStream, outputStream);
        }
    }

    /**
     * Returns the socket used for communication with the server.
     *
     * @return The socket object.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Returns the input stream associated with the socket.
     *
     * @return The input stream.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns the output stream associated with the socket.
     *
     * @return The output stream.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Returns the robot associated with the client.
     *
     * @return The robot object associated with the client.
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Sets the robot for the client.
     *
     * @param robot The robot object to associate with the client.
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    /**
     * Sends a message to the server.
     */
    public void sendMessage() {
        try {
            connectClientToServer();

            while (socket.isConnected()) {
                String userInput = textInterface.getUserInput();
                handleUserInput(userInput);
            }
        } catch (IOException e) {
            closeEverything(socket, inputStream, outputStream);
        }
    }

    /**
     * Handles the user input received from the client by creating a command that when executed will return a request.
     * The Request object is converted a JSON string and sent to server.
     * Ignores user input if reloading or repairing...
     * 
     * @param userInput The user input string.
     * @throws IOException If an I/O error occurs.
     */
    public void handleUserInput(String userInput) throws IOException {
        // create a command that when executed will return a request.
        if (!paused) {
            try {
                if (userInput.equalsIgnoreCase("help")) {
                    Command.help();
                    return;
                }
                else if (userInput.equalsIgnoreCase("clear")) {
                    Command.currentCommand = "clear";
                    Command.clear();
                    textInterface.output("terminal has been cleared.");
                    return;
                }
                else{
                    currentCommand = Command.create(userInput);
                }
            } catch (IllegalArgumentException e) {
                Command.currentCommand = "error";
                textInterface.output(e.getMessage());
                return;
            }
        }
        else { // repairing or reloading
            return;
        }

        // if the command is launch
        if (currentCommand instanceof LaunchCommand) {
            String result = instantiateRobot(userInput);
            if (result.equals("already launched")) {
                return;
            }
        }

        // if the command is repair or reload
        if (currentCommand instanceof RepairCommand || currentCommand instanceof ReloadCommand) {
            paused = true;
        }

        // user should not be able to do anything but 'quit' if robot is not launched.
        if (robot != null || currentCommand instanceof QuitCommand) {
            Request request = currentCommand.execute(robot);
            String requestJsonString = JsonHandler.serializeRequest(request);
            sendToServer(requestJsonString);
        }
        // robot has not been launched, and command is not launch or quit.
        else if (!(currentCommand instanceof QuitCommand)) {
            textInterface.output("Please launch a robot into the world first.");
        }
    }

    /**
     * Instantiates a robot based on the user input.
     * If user enters invalid type, Sniper is instantiated.
     * Will not instatiate if a robot has already been launched.
     *
     * @param userInput The user input string.
     */
    public String instantiateRobot(String userInput) {
        if (robot == null) { // user hasn't launched robot yet.
            String[] args = userInput.toLowerCase().trim().split(" ");
            switch (args[1]) {
                case "venom":
                    robot = new Venom(args[2]);
                    break;
                case "fighter":
                    robot = new Fighter(args[2]);
                    break;
                default:
                    robot = new Sniper(args[2]);
            }
            return "continue launching";
        } else {
            // robot has already been launched so break out of this method.4
            textInterface.output("you have already launched a robot into the world.");
            return "already launched";
        }
    }

    /**
     * Sends a request JSON string to the server.
     *
     * @param requestJsonString The JSON string representing the request.
     * @throws IOException If an I/O error occurs.
     */
    public void sendToServer(String requestJsonString) throws IOException {
        outToServer.println(requestJsonString);
        outToServer.flush();
    }

    /**
     * Establishes the initial connection between the client and the server.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void connectClientToServer() throws IOException {
        Request connectionRequest = new Request(String.valueOf(socket.getInetAddress()), "connect", new String[] {});
        String connectionRequestString = JsonHandler.serializeRequest(connectionRequest);
        sendToServer(connectionRequestString);
        Command.currentCommand = "connect";
    }

    /**
     * Listens for messages sent from the server in a separate thread.
     */
    public void listenFormessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseFromServer;

                while (socket.isConnected()) {
                    try {
                        responseFromServer = getResponseFromServer();
                        handleResponse(responseFromServer);
                    } catch (java.lang.StringIndexOutOfBoundsException e) {
                        closeEverything(socket, inputStream, outputStream);
                        System.exit(0);
                    } 
                    catch (IOException e) {
                        closeEverything(socket, inputStream, outputStream);
                        System.exit(0);
                    } 
                    catch (Exception e) {
                        e.printStackTrace();
                        closeEverything(socket, inputStream, outputStream);
                    }
                }
            }

        }).start();
    }

    /**
     * Retrieves the response from the server.
     *
     * @return The response string from the server.
     * @throws IOException If an I/O error occurs.
     */
    public String getResponseFromServer() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        String response = new String(buffer, 0, bytesRead);
        return response;
    }

    /**
     * Handles the response received from the server.
     *
     * @param response The response string from the server.
     */
    public void handleResponse(String response) {
        JsonNode responseJson = JsonHandler.deserializeJsonTString(response);
        JsonNode msgNode = responseJson.get("data").get("message");
        String msgStr = msgNode == null ? "" : msgNode.asText();

        // Handle initial connection response
        if (Command.currentCommand.equals("connect")) {
            handleConnectResponse(response);
            return;
        }

        // Handle client disconnection or shutdown response
        if (currentCommand instanceof QuitCommand || msgStr.contains("disconnected.")) {
            handleDisconnectResponse(msgStr);
            return;
        }

        // Handle response for being shot
        if (responseJson.get("result").asText().equals("OK") && msgStr.equals("You've been shot.")) {
            handleShotResponse(responseJson);
            return;
        }

        // Handle new robot launched
        if (robot != null && msgStr != null && msgStr.contains("new robot")) {
            handleNewRobotResponse(responseJson);
            return;
        }

        // Handle robots currently in the world
        if (robot != null && msgStr != null && msgStr.equals("robots currently in world")) {
            handleRobotsInWorldResponse(responseJson);
            return;
        }

        // Handle removal of an enemy
        if (robot != null && msgStr != null && msgStr.equals("remove enemy")) {
            handleRemoveEnemyResponse(responseJson);
            return;
        }

        // Handle enemy state change
        if (robot != null && msgStr != null && msgStr.equals("enemy state changed")) {
            handleEnemyStateChangeResponse(responseJson);
            return;
        }

        // Handle enemy firing gun
        if (robot != null && msgStr != null && msgStr.contains("enemy fired gun")) {
            handleEnemyFiredGunResponse(responseJson);
            return;
        }

        // Handle other responses
        handleOtherResponses(responseJson);
    }

    /**
     * Handles the initial connection response from the server.
     *
     * @param response The response string from the server.
     */
    private void handleConnectResponse(String response) {
        textInterface.output(response);
    }

    /**
     * Handles the response when the client or server disconnects.
     *
     * @param msgStr The message string indicating disconnection.
     */
    private void handleDisconnectResponse(String msgStr) {
        String outputStr = currentCommand instanceof QuitCommand ? "Shutting down..." : msgStr;
        textInterface.output(outputStr);
        closeEverything(getSocket(), inputStream, outputStream);
        System.exit(0);
    }

    /**
     * Handles the response when the client has been shot.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleShotResponse(JsonNode responseJson) {
        robot.setState(JsonHandler.updateState(responseJson));
        if (robot.getShields() < 0) {
            System.out.println("You are dead!!! GoodBye.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else {
            textInterface.output(responseJson.toString());
        }
    }

    /**
     * Handles the response when a new robot has been launched into the world.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleNewRobotResponse(JsonNode responseJson) {
        String enemyName = responseJson.get("data").get("robotName").asText();
        String enemyKind = responseJson.get("data").get("robotKind").asText();
        State enemyState = JsonHandler.getState(responseJson.get("data").get("robotState"));

        Robot enemyRobot = new Robot(enemyName, enemyKind, enemyState);
        robot.addEnemy(enemyRobot);
        robot.addEnemyName(enemyName);
    }

    /**
     * Handles the response listing all robots currently in the world.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleRobotsInWorldResponse(JsonNode responseJson) {
        JsonNode robotsNode = responseJson.get("data").get("robots");

        if (robot != null && robotsNode != null) {
            for (int i = 0; i < robotsNode.size(); i++) {
                String enemyName = robotsNode.get(i).get("robotName").asText();
                String enemyKind = robotsNode.get(i).get("robotKind").asText();
                State enemyState = JsonHandler.getState(robotsNode.get(i).get("robotState"));

                Robot enemyRobot = new Robot(enemyName, enemyKind, enemyState);
                robot.addEnemy(enemyRobot);
                robot.addEnemyName(enemyName);
            }
        }
    }

    /**
     * Handles the response when an enemy is removed.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleRemoveEnemyResponse(JsonNode responseJson) {
        String enemyName = responseJson.get("data").get("robotName").asText();
        robot.removeEnemy(enemyName);
    }

    /**
     * Handles the response when an enemy's state changes.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleEnemyStateChangeResponse(JsonNode responseJson) {
        String enemyName = responseJson.get("data").get("robotName").asText();
        State enemyState = JsonHandler.getState(responseJson.get("data").get("robotState"));
        robot.updateEnemyState(enemyName, enemyState);
    }

    /**
     * Handles the response when an enemy fires a gun.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleEnemyFiredGunResponse(JsonNode responseJson) {
        String enemyName = responseJson.get("data").get("robotName").asText();
        int bulletDistance = responseJson.get("data").get("distance").asInt();

        if (textInterface.getGui() != null && textInterface.getGui().getPlayer() != null) {
            textInterface.getGui().getEnemyPlayer(enemyName).fire(bulletDistance);
        }
    }

    /**
     * Handles all other responses from the server.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleOtherResponses(JsonNode responseJson) {
        if (responseJson.get("result").asText().equals("OK") && currentCommand != null) {
            switch (currentCommand.getName()) {
                case "launch":
                    robot.setState(JsonHandler.updateState(responseJson));
                    Robot.setReload(getReloadTime(responseJson));
                    Robot.setRepair(getRepairTime(responseJson));
                    Robot.setVisibility(getVisibility(responseJson));
                    textInterface.output(responseJson.toString());
                    break;
                case "forward":
                case "back":
                case "turn":
                case "fire":
                    robot.setState(JsonHandler.updateState(responseJson));
                    textInterface.output(responseJson.toString());
                    break;
                case "repair":
                    handleRepairResponse(responseJson);
                    break;
                case "reload":
                    handleReloadResponse(responseJson);
                    break;
                default:
                    textInterface.output(responseJson.toString());
            }
        } else {
            // handle error response for launch
            if (currentCommand instanceof LaunchCommand) {
                this.robot = null;
            }
            Command.currentCommand = "error";
            textInterface.output(responseJson.get("data").get("message").asText());
        }
    }

    /**
     * Handles the response for a repair command.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleRepairResponse(JsonNode responseJson) {
        System.out.println("Repairing...");
        try {
            Thread.sleep(Robot.getRepair() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.setState(JsonHandler.updateState(responseJson));
        textInterface.output(responseJson.toString());
        paused = false;
    }

    /**
     * Handles the response for a reload command.
     *
     * @param responseJson The JSON response from the server.
     */
    private void handleReloadResponse(JsonNode responseJson) {
        System.out.println("Reloading...");
        try {
            Thread.sleep(Robot.getReload() * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        robot.setState(JsonHandler.updateState(responseJson));
        textInterface.output(responseJson.toString());
        paused = false;
    }

    /**
     * Closes the socket, input stream, and output stream.
     *
     * @param socket        The socket to close.
     * @param inputStream   The input stream to close.
     * @param outputStream  The output stream to close.
     */
    public void closeEverything(Socket socket, InputStream inputStream, OutputStream outputStream) {
        try (socket; inputStream; outputStream) {
            // resources are automatically closed when the try block completes.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The entry point of the client application.
     *
     * @param args The command-line arguments.
     * @throws IOException If an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        Socket socket = args.length == 2?  
            new Socket(args[0], Integer.parseInt(args[1])) : new Socket("localhost", 5050);
        Client client = new Client(socket);
        client.listenFormessage();
        client.sendMessage();
    }

    /**
     * Retrieves the reload time from the server response JSON.
     *
     * @param responseJson The JSON response from the server.
     * @return The reload time in seconds. Returns 0 if the reload time is not present in the JSON.
     */
    int getReloadTime(JsonNode responseJson) {
        return responseJson.get("data").get("reload") != null ? responseJson.get("data").get("reload").asInt() : 0;
    }

    /**
     * Retrieves the repair time from the server response JSON.
     *
     * @param responseJson The JSON response from the server.
     * @return The repair time in seconds. Returns 0 if the repair time is not present in the JSON.
     */
    int getRepairTime(JsonNode responseJson) {
        return responseJson.get("data").get("repair") != null ? responseJson.get("data").get("repair").asInt() : 0;
    }

    /**
     * Retrieves the visibility from the server response JSON.
     *
     * @param responseJson The JSON response from the server.
     * @return The visibility in meters. Returns 0 if the visibility is not present in the JSON.
     */
    int getVisibility(JsonNode responseJson) {
        return responseJson.get("data").get("visibility") != null ? responseJson.get("data").get("visibility").asInt() : 0;
    }

}
