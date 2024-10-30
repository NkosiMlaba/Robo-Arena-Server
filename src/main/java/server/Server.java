package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import domain.ClientHandler;
import domain.world.Obstacle;
import domain.world.SquareObstacle;
import domain.world.World;

/**
 * The Server class represents a server that listens for client connections and manages client handlers.
 * It starts the server, accepts client connections, and creates a separate client handler thread for each client.
 */
public class Server {

    
    private ServerSocket serverSocket; 
    private World world;
    private static int port;
    private static int eachSide;
    private static boolean isSideChanged = false;
    private static List<Obstacle> obstacles = new ArrayList<>();

    //
    // comment
    /**
     * Constructs a new Server object.
     *
     * @param serverSocket the server socket
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        startWorld();
    }

    /**
     * Starts the server by initializing server commands and accepting client connections.
     * It prints the server's IP address and port, starts a thread to handle server commands,
     * and continuously accepts new client connections, each handled in a separate thread.
     */
    public void startServer() {
        startServerCommandsThread();
        acceptClientConnections();
    }

    /**
     * Initializes and starts a thread for handling server commands.
     * Prints the server's IP address and port.
     */
    private void startServerCommandsThread() {
        try {
            int currentPort = port != 0 ? port : 5000;
            System.out.println("SERVER " + port);
            System.out.println("SERVER <" + InetAddress.getLocalHost().getHostAddress() + "> " + ": Listening on port " + currentPort + "...");
            
            ServerHandler serverCommands = new ServerHandler(world);
            Thread serverThread = new Thread(serverCommands);
            serverThread.start();
        } catch (UnknownHostException e) {
            System.err.println("Error obtaining the host address.");
            e.printStackTrace();
        }
    }

    /**
     * Continuously accepts new client connections and starts a thread to handle each client.
     */
    private void acceptClientConnections() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // Accept a client connection
                System.out.println("A new client" + " <" + socket.getInetAddress().getHostName() + "> " + "has connected!");
                
                ClientHandler clientHandler = new ClientHandler(socket, world);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error accepting client connections.");
            e.printStackTrace();
        }
    }

    /**
     * Closes the server socket.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the `world` object based on the current configuration parameters.
     * 
     */
    public void startWorld() {
        
        if (isSideChanged == true && obstacles != null ) {
            world = new World(eachSide, obstacles);
            return;
        }
        else if (isSideChanged == true ) {
            world = new World(eachSide);
            return;
        }
        else {
            world = new World();
            return;
        }
    }

    /**
     * The main method to run the server.
     * Creates a server socket, creates a Server instance, and starts the server.
     *
     * @param args command line arguments (not used)
     * @throws IOException if an I/O error occurs while creating the server socket
     */
    public static void main(String[] args) throws IOException {
        setWorldParameters(args);
        ServerSocket serverSocket = new ServerSocket(port);
        Server server =  new Server(serverSocket);
        server.startServer();
    }

    /**
     * Sets the parameters for the world based on the provided command-line arguments.
     * The method parses the arguments to configure the server's port, world size, and obstacles.
     *
     * @param args the command-line arguments used to configure the server
     */
    public static void setWorldParameters(String[] args) {
        if (args == null) {
            return;
        }
        
        // Handle port parameter
        setPortParameter(args);

        // Handle world size parameter
        setWorldSizeParameter(args);

        // Handle obstacle parameter
        setObstacleParameter(args);
    }

    /**
     * Sets the server port based on the command-line arguments.
     *
     * @param args the command-line arguments
     */
    private static void setPortParameter(String[] args) {
        if (args.length > 1 && "-p".equals(args[0])) {
            try {
                int numberGiven = Integer.parseInt(args[1]);
                if (numberGiven >= 0 && numberGiven < 9999) {
                    port = numberGiven;
                } else {
                    System.out.println("Invalid port number. Using default port.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number format. Using default port.");
            }
        }
    }

    /**
     * Sets the world size based on the command-line arguments.
     *
     * @param args the command-line arguments
     */
    private static void setWorldSizeParameter(String[] args) {
        if (args.length > 3 && "-s".equals(args[2])) {
            try {
                int numberGiven = Integer.parseInt(args[3]);
                if (numberGiven > 0 && numberGiven < 9999) {
                    isSideChanged = true;
                    eachSide = numberGiven;
                } else {
                    System.out.println("Invalid world size. Using default size.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid world size format. Using default size.");
            }
        }
    }

    /**
     * Sets the obstacles based on the command-line arguments.
     *
     * @param args the command-line arguments
     */
    private static void setObstacleParameter(String[] args) {
        if (args.length > 5 && "-o".equals(args[4])) {
            try {
                String obstaclePosition = args[5];
                if (validateIfObstacleFormat(obstaclePosition)) {
                    String[] position = obstaclePosition.split(",");
                    int x = Integer.parseInt(position[0]);
                    int y = Integer.parseInt(position[1]);
                    obstacles.add(new SquareObstacle(x, y));
                } else {
                    System.out.println("Invalid obstacle format. Using default obstacles.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid obstacle format. Using default obstacles.");
            } catch (Exception e) {
                System.out.println("Error processing obstacle parameters. Using default obstacles.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Validates whether the given obstacle position string is in the correct format.
     * The expected format is two non-negative integers separated by a comma (e.g., "12,34").
     *
     * @param position the string representing the obstacle position to validate
     * @return true if the position is in the correct format, false otherwise
     */
    public static boolean validateIfObstacleFormat (String position) {
        return position.matches("([0-9]+),([0-9]+)");
    }

    /**
     * Gets the current world
     * 
     * @return current world started in the server
     */
    public World getWorld () {
        return this.world;
    }

    /**
     * Gets the current running port
     * 
     * @return the port
     */
    static public int getPort () {
        return port;
    }

    
}