package com.itachallenge.score.mqclient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.itachallenge.score.dto.TestParamsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.score.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreMicroZmqClient {

    private static final Logger log = LoggerFactory.getLogger(ScoreMicroZmqClient.class);

    @Value("${sandbox.solutions-dir}")
    private String solutionsDir;

    private final String SOCKET_ADDRESS = "tcp://challenge-micro:5555";  // Address of Challenge Micro's ZMQ Server
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CompletableFuture<Void> requestTestParams(UUID challengeId, UUID languageId, UUID solutionId) {
        return CompletableFuture.runAsync(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket socket = context.createSocket(SocketType.DEALER); //(SocketType.REQ)
                socket.connect(SOCKET_ADDRESS);

                TestParamsRequest request = TestParamsRequest.builder()
                        .uuidChallenge(challengeId)
                        .uuidLanguage(languageId)
                        .build();


                byte[] requestBytes = objectMapper.writeValueAsBytes(request);
                log.info("Sending request for test parameters: {}", new String(requestBytes));

                socket.send(requestBytes);

                byte[] replyBytes = socket.recv(0);
                if (replyBytes == null || replyBytes.length == 0) {
                    throw new RuntimeException("Empty response from Challenge Micro server");
                }

                Map<String, Object> testParams = objectMapper.readValue(replyBytes, new TypeReference<Map<String, Object>>() {});

                FileUtil.createTestParamsFile(testParams, solutionsDir);

            } catch (Exception e) {
                log.error("Error interacting with the Challenge Micro server", e);
            }
        }, executorService);
    }
}


