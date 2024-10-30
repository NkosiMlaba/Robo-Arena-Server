package client.commands;


import client.robots.Robot;
import client.request.Request;


public class LaunchCommand extends Command{
    private String make;
    private String robotName;

    /**
     * Constructs a LaunchCommand object with the specified make and robot name.
     *
     * @param make      The make of the robot.
     * @param robotName The name of the robot.
     */
    public LaunchCommand(String make, String name) {
        super("launch");
        this.make =  make;
        this.robotName = name;
    }

    @Override
    public Request execute(Robot target) {
        String sheilds = String.valueOf(target.getShields());
        String shots = String.valueOf( target.getShots());
        return new Request(robotName, "launch", new String[]{make, sheilds, shots});
    }

    /**
     * Gets the make of the robot
     * 
     * @return the kind of robot launch
     */
    public String getKind(){
        return this.make;
    }
}