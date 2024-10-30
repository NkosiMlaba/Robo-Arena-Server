package client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.Socket;

public class RobotWorldJsonClient implements RobotWorldClient {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Socket socket;
    private PrintStream out;
    private BufferedReader in;
    private InputStream inputStream;

    @Override
    public void connect(String ipAddress, int port) {
        try {
            socket = new Socket(ipAddress, port);
            out = new PrintStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.inputStream = socket.getInputStream();
        } catch (IOException e) {
            //error connecting should just throw Runtime error and fail test
            throw new RuntimeException("Error connecting to Robot Worlds server.", e);
        }
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void disconnect() {
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            //error connecting should just throw Runtime error and fail test
            throw new RuntimeException("Error disconnecting from Robot Worlds server.", e);
        }
    }

    @Override
    public JsonNode sendRequest(String requestJsonString) {
        try {
            out.println(requestJsonString);
            out.flush();
            // return OBJECT_MAPPER.readTree("{\"response\": \"DSAD\"}");
            JsonNode responseJson;
            
            while (true) {
                responseJson = OBJECT_MAPPER.readTree(getResponseFromServer());
                if (responseJson.get("type") == null) {
                    break;
                }

                if ("robot".equals(responseJson.get("type").asText())) {
                    // System.out.println(responseJson);
                    break;
                }
            }
            return responseJson;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing server response as JSON.", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading server response.", e);
        }
    }

    @Override
    public String sendRequestAsString(String requestString) {
        try {
            out.println(requestString);
            out.flush();
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Error reading server response.", e);
        }
    }

    @Override
    public String getResponseFromServer() throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        String response = new String(buffer, 0, bytesRead);
        return response;
    }
}