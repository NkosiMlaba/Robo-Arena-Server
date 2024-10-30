package domain.world.util;

import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The State class represents the state of an object in the world.
 * It contains information such as position, direction, shields, shots, and status.
 */
public class State {

    private int[] position;
    private String direction;
    private int shields;
    private int shots;
    private String status;
    private HashMap<String, Object> state;

    /**
     * Constructs a new State object with the specified position, direction, shields, shots, and status.
     *
     * @param position  The position of the object as an array of integers [x, y].
     * @param direction The direction of the object.
     * @param shields   The shields of the object.
     * @param shots     The shots of the object.
     * @param status    The status of the object.
     */
    public State(int[] position, String direction, int shields, int shots, String status) {
        this.position = position;
        this.direction = direction;
        this.shields = shields;
        this.shots = shots;
        this.status = status;
        this.setState();
    }

    /**
     * Sets the state of the object as a HashMap.
     * This method is used internally to initialize the state.
     */
    private void setState() {
        this.state = new HashMap<String, Object>();
        this.state.put("position", position);
        this.state.put("direction", direction);
        this.state.put("shields", shields);
        this.state.put("shots", shots);
        this.state.put("status", status);
    }

    /**
     * Returns the state of the object as a HashMap.
     *
     * @return The state of the object as a HashMap.
     */
    @JsonIgnore
    public HashMap<String, Object> getStateAsHashMap() {
        return this.state;
    }

    /**
     * Returns a string representation of the State object.
     *
     * @return A string representation of the State object.
     */
    @Override
    public String toString() {
        return "State{" +
                "position=" + Arrays.toString(position) +
                ", direction='" + direction + '\'' +
                ", shields=" + shields +
                ", shots=" + shots +
                ", status='" + status + '\'' +
                '}';
    }
}
