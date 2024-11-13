package com.itachallenge.score.mqclient;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.itachallenge.score.helper.ObjectSerializer;

public class ScoreMicroZmqClient {
    private final String SOCKET_ADDRESS = "tcp://challenge-micro:5555";  // Address of Challenge Micro's ZMQ Server

    @Value("${file.location}")
    private final String FILE_LOCATION;  // Parameterized location for storing test params
    private static final Logger log = LoggerFactory.getLogger(ScoreMicroZmqClient.class);

    public ScoreMicroZmqClient(String fileLocation) {
        this.FILE_LOCATION = fileLocation;  // Set the location from configuration
    }

    public CompletableFuture<Map<String, Object>> requestTestParams(String challengeId, String languageId) {
        return CompletableFuture.supplyAsync(() -> {
            try (ZContext context = new ZContext()) {
                // Create a socket as client
                ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
                socket.connect(SOCKET_ADDRESS);

                // Prepare request message
                String request = "GET_TEST_PARAMS " + challengeId + " " + languageId;
                socket.send(request.getBytes(ZMQ.CHARSET));

                // Receive response from server
                byte[] reply = socket.recv(0);

                // Deserialize response into Map<String, Object> (test params)
                Map<String, Object> testParams = ObjectSerializer.deserialize(reply, Map.class);

                // Create a text file with key-value pairs
                createTestParamsFile(testParams);

                return testParams;
            } catch (Exception e) {
                log.error("Error during ZMQ communication", e);
                return null;
            }
        });
    }

    private void createTestParamsFile(Map<String, Object> testParams) throws IOException {
        // Define the path to store the file
        String filePath = Paths.get(FILE_LOCATION, "test_params.txt").toString();
        File file = new File(filePath);

        // Create a StringBuilder to store key-value pairs
        StringBuilder content = new StringBuilder();

        // Write each key-value pair to the StringBuilder
        for (Map.Entry<String, Object> entry : testParams.entrySet()) {
            content.append(entry.getKey()).append("=").append(entry.getValue()).append(System.lineSeparator());
        }

        // Write content to the file using Apache Commons FileUtils
        FileUtils.writeStringToFile(file, content.toString(), StandardCharsets.UTF_8);
    }
}
