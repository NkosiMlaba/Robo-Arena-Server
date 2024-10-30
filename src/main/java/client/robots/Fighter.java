package client.robots;

public class Fighter extends Robot {

     /**
     * Constructs a new Fighter robot with the specified name.
     *
     * @param name The name of the Fighter robot.
     */
    public Fighter(String name) {
        super(name);
        setKind("fighter");
        setShields(3);
        setShots(25);
    }
}
