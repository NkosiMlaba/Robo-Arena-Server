package client.robots;

public class Venom extends Robot{

    /**
     * Constructs a new Venom robot with the specified name.
     *
     * @param name The name of the Venom robot.
     */
    public Venom(String name) {
        super(name);
        setKind("venom");
        setShields(15);
        setShots(5);
    }
}
