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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQException;

import java.io.IOException;
import java.util.Optional;


@Component
public class ZMQServer {

    private ZContext context;
    private final String SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);

    @Autowired
    ObjectSerializer objectSerializer;

    public ZMQServer(@Value("${zeromq.socket.address}") String socketAddress){
        this.SOCKET_ADDRESS = socketAddress;
    }

    @PostConstruct
    public void init() {
        log.debug("Initializing ZMQServer with SOCKET_ADDRESS: {}", SOCKET_ADDRESS);
        if (SOCKET_ADDRESS == null || SOCKET_ADDRESS.isEmpty()) {
            log.error("SOCKET_ADDRESS is not set");
            return;
        }
        log.info("Starting ZMQ Server");
        context = new ZContext();
        new Thread(this::run).start();
    }

    public void run(){
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind(SOCKET_ADDRESS);

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);

                Optional<ScoreRequestDto> request = Optional.empty();
                try {
                    request = Optional.of(objectSerializer.deserialize(reply, ScoreRequestDto.class));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                if (request.isPresent()) {
                    log.info("Received: [" + request.get().getUuidChallenge() + "]");
                    ScoreResponseDto responseDto = calculateScore(request.get());

                    Optional<byte[]> response = Optional.empty();
                    try {
                        response = Optional.of(objectSerializer.serialize(responseDto));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }

                    socket.send(response.orElse(new byte[0]), 0);
                }
            }
        } catch (ZMQException e) {
            log.error("ZMQ Exception: " + e.getMessage());
        } catch (Exception e) {
            log.error("Exception: " + e.getMessage());
        }
    }

    private ScoreResponseDto calculateScore(ScoreRequestDto request) {
        // Implement your score calculation logic here
        ScoreResponseDto response = new ScoreResponseDto();
        response.setUuidChallenge(request.getUuidChallenge());
        response.setUuidLanguage(request.getUuidLanguage());
        response.setSolutionText(request.getSolutionText());
        response.setScore(100); // Example score
        response.setErrors(null);
        response.setCompilationMessage("Success");
        response.setExpectedResult("Expected Result");
        return response;
    }

    public void cleanup() {
        if (context != null) {
            context.close();
        }
    }

    public boolean isRunning() {
        return context != null && !context.isClosed();
    }
}