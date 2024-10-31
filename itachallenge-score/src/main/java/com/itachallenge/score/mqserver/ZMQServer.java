package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Optional;


@Component
public class ZMQServer {

    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);
    @Getter
    private volatile boolean running = true; // Flag to control the server loop
    private Thread serverThread;
    private final ObjectSerializer objectSerializer;

    public ZMQServer(ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    @PostConstruct
    public void init() {
        log.info("Starting ZMQ Server");
        serverThread = new Thread(this::run);
        serverThread.start();
    }

    public void run() {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (running) {
                byte[] reply = socket.recv(0);
                Optional<ScoreRequestDto> request = Optional.empty();

                try {
                    request = Optional.of(objectSerializer.deserialize(reply, ScoreRequestDto.class));
                } catch (IOException e) {
                    log.error("Deserialization error: {}", e.getMessage());
                    // Optionally send an error response back to the client
                    continue; // Skip to the next iteration
                }

                request.ifPresent(req -> {
                    log.info("Received: [{}]", req);
                    ScoreResponseDto responseDto = ScoreResponseDto.builder()
                            .uuidChallenge(req.getUuidChallenge())
                            .uuidLanguage(req.getUuidLanguage())
                            .solutionText(req.getSolutionText())
                            .score(99) // TODO: calculate actual score
                            .errors("xxx") // TODO: calculate actual errors
                            .build();

                    try {
                        byte[] response = objectSerializer.serialize(responseDto);
                        socket.send(response, 0);
                    } catch (JsonProcessingException e) {
                        log.error("Serialization error: {}", e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error in ZMQ Server: {}", e.getMessage());
        }
    }

    @PreDestroy
    public void cleanup() {
        log.info("Stopping ZMQ Server");
        running = false; // Stop the server loop
        if (serverThread != null) {
            serverThread.interrupt(); // Interrupt the thread if it's blocked
            try {
                serverThread.join(); // Wait for the thread to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }
    }
}