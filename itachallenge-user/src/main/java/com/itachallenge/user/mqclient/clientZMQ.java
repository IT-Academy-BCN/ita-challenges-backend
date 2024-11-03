package com.itachallenge.user.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.user.helper.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.itachallenge.user.dtos.zmq.ScoreRequestDto;
import com.itachallenge.user.dtos.zmq.ScoreResponseDto;

import java.io.IOException;

@Component
public class clientZMQ {
    private final ZContext context;
    private final String SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(clientZMQ.class);

    @Autowired
    ObjectSerializer objectSerializer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public clientZMQ(ZContext context, @Value("${zeromq.socket.address}") String socketAddress) {
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
    }

    public CompletableFuture<ScoreResponseDto> sendScoreRequest(ScoreRequestDto requestDto) {
        return CompletableFuture.supplyAsync(() -> {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect(SOCKET_ADDRESS);

            Optional<byte[]> request = Optional.empty();
            try {
                request = Optional.of(objectSerializer.serialize(requestDto));
            } catch (JsonProcessingException jpe) {
                log.error(jpe.getMessage());
            }

            socket.send(request.orElse(new byte[0]), 0);

            byte[] reply = socket.recv(0);
            Optional<ScoreResponseDto> response = Optional.empty();
            try {
                response = Optional.of(objectSerializer.deserialize(reply, ScoreResponseDto.class));
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            return response.orElse(null);
        }, executorService);
    }
}