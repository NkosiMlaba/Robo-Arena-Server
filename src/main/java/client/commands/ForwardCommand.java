package client.commands;

import client.request.Request;
import client.robots.Robot;

public class ForwardCommand extends Command {
    private String argument;

    /**
     * Constructs a ForwardCommand object with the specified argument.
     *
     * @param argument The argument for the forward command.
     */
    public ForwardCommand(String argument) {
        super("forward");
        this.argument = argument;
    }

    @Override
    public Request execute(Robot target) {
        return new Request(target.getName(), "forward", new String[]{argument});
    }
}