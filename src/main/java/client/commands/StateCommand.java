package client.commands;

import client.robots.Robot;
import client.request.Request;

/**
 * The StateCommand class represents a command to reload a robot.
 */
public class StateCommand extends Command{

    /**
     * Constructs a new StateCommand.
     */
   public StateCommand(){
       super("state");
   }

   @Override
   public Request execute(Robot target) {
       return new Request(target.getName(), "state", new String[]{});
   }
}
