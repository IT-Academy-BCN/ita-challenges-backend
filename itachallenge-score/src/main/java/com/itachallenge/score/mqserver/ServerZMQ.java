package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class ServerZMQ {
    private static final Logger log = LoggerFactory.getLogger(ServerZMQ.class);

    private final ZContext context;
    private final String serverAddress;

    @Autowired
    private ObjectMapper objectMapper;

    public ServerZMQ(ZContext context, @Value("${mqserver.score.address}") String serverAddress) {
        this.context = context;
        this.serverAddress = serverAddress;
    }

    @PostConstruct
    public void init() {
        log.info("Server address: {}", serverAddress);
        log.info("Starting ZeroMQ Server");
        new Thread(this::run).start();
    }

    public void run() {
        try (ZMQ.Socket serverSocket = context.createSocket(SocketType.REP)) {
            serverSocket.bind(serverAddress);
            log.info("ZeroMQ Server initialized and listening at {}", serverAddress);

            while (!Thread.currentThread().isInterrupted()) {
                byte[] requestBytes = serverSocket.recv(0);
                Optional<ScoreRequestDto> requestDto = Optional.empty();

                try {
                    requestDto = Optional.of(objectMapper.readValue(requestBytes, ScoreRequestDto.class));
                } catch (IOException e) {
                    log.error("Failed to deserialize request: {}", e.getMessage());
                }

                requestDto.ifPresent(dto -> log.info("Received request for user ID: {}", dto.getUserId()));
                ScoreResponseDto responseDto = processScoreRequest(requestDto.orElse(new ScoreRequestDto()));

                byte[] responseBytes;
                try {
                    responseBytes = objectMapper.writeValueAsBytes(responseDto);
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize response: {}", e.getMessage());
                    responseBytes = new byte[0];  // Fallback to an empty response
                }

                serverSocket.send(responseBytes, 0);
                log.info("Sent response: {}", responseDto);
            }
        }
    }

    private ScoreResponseDto processScoreRequest(ScoreRequestDto requestDto) {
        // Simulate score processing logic
        ScoreResponseDto responseDto = new ScoreResponseDto();
        responseDto.setScore(calculateScore(requestDto.getParameters()));
        return responseDto;
    }

    private int calculateScore(Map<String, Object> parameters) {
        // Dummy calculation based on input parameters
        return parameters.size() * 10;  // Example logic for demonstration
    }

    @PreDestroy
    public void cleanup() {
        log.info("ZeroMQ Server closing");
        context.close(); // Ensure context is closed when the application is shutting down
    }
}
