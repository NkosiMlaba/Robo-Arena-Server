package client.robots;

public class Sniper extends Robot{

    /**
     * Constructs a new Sniper robot with the specified name.
     *
     * @param name The name of the Sniper robot.
     */
    public Sniper(String name) {
        super(name);
        setKind("sniper");
        setShields(1);
        setShots(20);
    }
}
