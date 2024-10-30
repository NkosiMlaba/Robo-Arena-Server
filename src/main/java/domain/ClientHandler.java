package domain;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

import domain.commands.Command;
import domain.commands.LaunchCommand;
import domain.response.*;
import domain.world.Robot;
import domain.world.World;
import json.JsonHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The ClientHandler class represents a client handler that manages the communication with a client connected to the server.
 * It handles requests from the client, executes commands, and sends responses back to the client.
 */
public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHanders = new ArrayList<>();
    // public static ArrayList<Robot> robots = new ArrayList<>();
    private Socket socket;
    private String robotName;
    private World world;
    private Robot robot;
    private String currentCommand;
    private boolean launched = false;

    // use OutputStream & InputStream instead of bufferedReader & bufferedWriter
    private OutputStream outputStream;
    private InputStream inputStream;

    private PrintStream out;

    /**
     * Constructs a new ClientHandler object.
     * Initializes the socket, sets up input/output streams, and handles the initial connect request from the client.
     *
     * @param socket the client socket
     * @param world  the world instance
     */
    public ClientHandler(Socket socket, World world) {
        try {
            this.socket = socket;
            this.world = world;
            clientHanders.add(this);
            this.outputStream = socket.getOutputStream();
            this.inputStream = socket.getInputStream();
            this.out = new PrintStream(socket.getOutputStream());
        } 
        catch (IOException e) {
            closeEverything(socket, inputStream, outputStream);
        }
    }
    
    /**
     * Returns the input stream associated with the client socket.
     *
     * @return the input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns the output stream associated with the client socket.
     *
     * @return the output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Returns the client socket.
     *
     * @return the client socket
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * Returns the world instance associated with the client handler.
     *
     * @return the world instance
     */
    public String getName() {
        return this.robotName;
    }

    /**
     * Returns the world instance associated with the client handler.
     *
     * @return the world instance
     */
    public World getWorld() {
        return this.world;
    }

    public void setLaunched(boolean launched) {
        this.launched = launched;
    }

    public boolean getLaunched() {
        return launched;
    }
    
    /**
     * Runs the client handler thread.
     * Listens for requests from the client, handles the requests, and sends responses back to the client.
     * Closes the socket and removes the client handler when the client disconnects.
     */
    @Override
    public void run() {
        String requestFromCleint;
        while (socket.isConnected()) {
            try {
                // get request from client.
                requestFromCleint = getRequestFromClient();
                System.out.println("Request from client: " + requestFromCleint);
                if (JsonHandler.isJsonString(requestFromCleint)) {
                    handleRequest(requestFromCleint); 
                }
            } catch (IOException e) {
                closeEverything(socket, inputStream, outputStream);
                break;
            }
        }
        // if client disconnects unexpecedly.
        closeEverything(socket, inputStream, outputStream);
        removeClientHandler();
        world.removeRobot(robot);
    }

    /**
     * Retrieves a request from the client.
     *
     * @return the request string from the client
     * @throws IOException if an I/O error occurs while reading the request
     */
    public String getRequestFromClient() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead == -1) { throw new IOException("Client disconnected");}
        String request = new String(buffer, 0, bytesRead); // No data was read, so return an empty string
        return request; 
    }

    /**
     * Handles a request from the client.
     * Executes the corresponding command and sends the response back to the client.
     *
     * @param request the request string from the client
     */
    public void handleRequest(String request) {
        try {
            Command newCommand = Command.create(request);
            Response response = newCommand.execute(this, world);

            String responseJsonString = JsonHandler.serializeResponse(response);
            
            sendToClient(responseJsonString);

            // if command is 'launch', send a list of all robots currently in world
            if (robot != null && newCommand instanceof LaunchCommand) {                
                List<HashMap<String, Object>> robotsList = new ArrayList<>();

                for (ClientHandler cH: ClientHandler.clientHanders) {
                    if (cH.getRobot() != null && cH!= this) { 
                        HashMap<String, Object> robotInfo = new HashMap<>();
                        robotInfo.put("robotName", cH.getRobot().getName());
                        robotInfo.put("robotKind", cH.getRobot().getKind());
                        robotInfo.put("robotState", cH.getRobot().getState());
                        robotsList.add(robotInfo);
                    }
                }
                
                Response message = new StandardResponse(new HashMap<>(){{
                    put("message", "robots currently in world");
                    put("robots", robotsList);
                }}, null, "gui");
                sendToClient(JsonHandler.serializeResponse(message));
            }

            // if command is 'quit' disconnect everything.
            if (currentCommand.equals("quit")) {
                closeEverything(getSocket(), inputStream, outputStream);
            }

        } 
        catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Unsupported command");
            String responseJsonString = JsonHandler.serializeResponse(errorResponse);
            sendToClient(responseJsonString);
        }
        catch (NullPointerException e) {}
    }

    /**
     * Sets the robot associated with the client handler.
     *
     * @param robot the robot to set
     */
    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    /**
     * Returns the robot associated with the client handler.
     *
     * @return the robot
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Sets the current command being executed by the client handler.
     *
     * @param command the current command
     */
    public void setCurrentCommand(String command) {
        this.currentCommand = command;
    }

    /**
     * Sends a message to the client.
     *
     * @param message the message to send
     */
    public void sendToClient(String message) {
            this.out.println(message);
            System.out.println("Response sent: " + message);
    }

    /**
     * Removes the client handler from the list of active client handlers.
     */
    public void removeClientHandler() {
        clientHanders.remove(this);
    }

    /**
     * Closes the client handler by closing the socket and input/output streams.
     *
     * @param socket        the client socket
     * @param inputStream   the input stream
     * @param outputStream  the output stream
     */
    public void closeEverything(Socket socket, InputStream inputStream, OutputStream outputStream) {
        removeClientHandler();
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string representation of the client handler.
     *
     * @return a string representation of the client handler
     */
    @Override
    public String toString() {
        return this.robotName;
    }

    /**
     * Returns a list of all active client handlers.
     *
     * @return a list of all active client handlers
     */
    public static List<ClientHandler> getClientHandlers() {
        return clientHanders;
    }
}
