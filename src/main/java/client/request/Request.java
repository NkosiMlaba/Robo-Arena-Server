package client.request;

import java.util.Arrays;

/**
 * Represents a request sent to the server by a client.
 */
public class Request {
    private String robot;
    private String command;
    private String[] arguments;

    /**
     * Constructs a new Request object with the specified robot name, command, and arguments.
     *
     * @param robotName The name of the robot associated with the request.
     * @param command The command to be executed.
     * @param arguments The arguments for the command.
     */
    public Request(String robotName, String command, String[] arguments) {
        this.robot = robotName;
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * Returns the command of the request.
     *
     * @return The command.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns the robot name associated with the request.
     *
     * @return The robot name.
     */
    public String getRobot() {
        return robot;
    }

    /**
     * Returns the arguments of the request.
     *
     * @return The arguments.
     */
    public String[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "Request{" +
                "robotName='" + robot + '\'' +
                ", command='" + command + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
