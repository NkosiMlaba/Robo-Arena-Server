package client.json;

import client.request.Request;
import client.robots.util.State;
import client.userInterface.util.Position;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes a Request object into a JSON string.
     *
     * @param request The Request object to be serialized.
     * @return The JSON string representation of the Request object.
     */
    public static String serializeRequest(Request request) {
        String jsonString = null;
        try {
            // serialize java response object into json string.
            jsonString = objectMapper.writeValueAsString(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * Deserializes a JSON file into a JsonNode object.
     *
     * @param file The JSON file to be deserialized.
     * @return The JsonNode representing the JSON file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static JsonNode deserializeJsonTString(String jsonString) {
        JsonNode jsonNode = null;
        try{
            // deserialize json string into JsonNode.
            jsonNode =  objectMapper.readTree(jsonString);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return jsonNode;
    }

    /**
     * Deserializes a JSON string into a list of Position objects representing obstacles.
     *
     * @param jsonString The JSON string to be deserialized.
     * @return The list of Position objects representing obstacles.
     */
    public static List<Position> deserializeObstacles(JsonNode obstaclesArray) {
        List<Position> obstacles = new ArrayList<>();

        for (int i = 0; i < obstaclesArray.size(); i++) {
            JsonNode obstacle = obstaclesArray.get(i);
            int x = obstacle.get("x").asInt();
            int y = obstacle.get("y").asInt();
            Position position = new Position(x, y);
            obstacles.add(position);
        }

        return obstacles;

    }

    /**
     * Converts a JSON string representing an integer array into an actual int array.
     *
     * @param intArray The JSON string representing an integer array.
     * @return The int array.
     */
    public static int[] convertToIntArray(String intArray) {
        int[] result = null;

        try {
            result = objectMapper.readValue(intArray, int[].class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Updates the State object based on the provided response JsonNode.
     * used upadate the state of a robot.
     *
     * @param responseJson The response JsonNode.
     * @return The updated State object.
     */
    public static State updateState(JsonNode responseJson) {
        State state = null;
        try {
            state = objectMapper.readValue(responseJson.get("state").toString(), State.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return state;
    }

    /**
     * Retrieves a State object from a given JSON node.
     *
     * @param stateNode The JSON node containing the state information.
     * @return The State object extracted from the JSON node.
     *
     * @throws JsonMappingException If there is a problem mapping the JSON node to a State object.
     * @throws JsonProcessingException If there is a problem processing the JSON node.
     */
    public static State getState(JsonNode stateNode) {
        State state = null;
        try {
            state = objectMapper.readValue(stateNode.toString(), State.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return state;
    }
    /**
     * Checks if a given string is a valid JSON representation.
     *
     * @param string The string to be checked.
     * @return True if the string is a valid JSON representation, false otherwise.
     *
     */
    public static Boolean isJsonString(String string) {
        try {
            objectMapper.readTree(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
