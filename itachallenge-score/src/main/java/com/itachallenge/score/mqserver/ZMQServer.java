package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.IOException;



@Component
public class ZMQServer{

    private final ZContext context;
    private final String socketAddress;
    private final ObjectSerializer objectSerializer;
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);
    private volatile boolean running = true;
    private Thread serverThread;

    @Autowired
    public ZMQServer(ZContext context, String socketAddress, ObjectSerializer objectSerializer) {
        this.context = context;
        this.socketAddress = socketAddress;
        this.objectSerializer = objectSerializer;
    }

    public void start() {
        log.info("Starting ZMQ Server");
        serverThread = new Thread(this::run);
        serverThread.start();
    }

    public void stop() {
        running = false;
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        ZMQ.Socket socket = context.createSocket(SocketType.REP);
        try {
            socket.bind(this.socketAddress);
            while (running && !Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                if (reply == null) continue;

                processMessage(reply, socket);
            }
        } catch (Exception e) {
            log.error("Unexpected error in ZMQServer", e);
        } finally {
            socket.close();
            context.close();
            running = false;
        }
    }

    private void processMessage(byte[] reply, ZMQ.Socket socket) {
        try {
            ScoreRequestDto requestDto = deserializeMessage(reply);
            log.info("Received: [{}]", requestDto);

            ScoreResponseDto responseDto = processRequest(requestDto);
            byte[] responseBytes = serializeMessage(responseDto);
            socket.send(responseBytes, 0);
        } catch (IOException e) {
            log.error("Failed to process message", e);
            stop();
        }
    }

    private ScoreRequestDto deserializeMessage(byte[] message) throws IOException {
        return objectSerializer.deserialize(message, ScoreRequestDto.class);
    }

    private byte[] serializeMessage(ScoreResponseDto responseDto) throws JsonProcessingException {
        return objectSerializer.serialize(responseDto);
    }

    private ScoreResponseDto processRequest(ScoreRequestDto requestDto) {
        return ScoreResponseDto.builder()
                .uuidChallenge(requestDto.getUuidChallenge())
                .uuidLanguage(requestDto.getUuidLanguage())
                .solutionText(requestDto.getSolutionText())
                .score(99)
                .errors("xxx")
                .compilationMessage("Compilation Message Text")
                .expectedResult("Expected Result Text")
                .build();
    }
}