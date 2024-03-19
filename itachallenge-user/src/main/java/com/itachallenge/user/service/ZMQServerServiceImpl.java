package com.itachallenge.user.service;

import com.itachallenge.challenge.mqclient.ZMQClient;
import com.itachallenge.user.dtos.zmq.ChallengeRequestDto;
import com.itachallenge.user.mqserver.ZMQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ZMQServerServiceImpl implements IZMQServerService{
    private static final Logger log = LoggerFactory.getLogger(ZMQServerServiceImpl.class);

    private final ZMQClient zmqClient;

    public ZMQServerServiceImpl(ZMQClient zmqClient) {
        this.zmqClient = zmqClient;
    }


    @Override
    public CompletableFuture<Object> respondToChallengeRequest(UUID challengeId) {
        log.info("Received request from Challenge for challenge with ID: {}", challengeId);

        // Create ChallengeRequestDto to send to Challenge microservice
        ChallengeRequestDto requestDto = ChallengeRequestDto.builder()
                .challengeId(challengeId)
                .build();

        // Send request to Challenge microservice
        CompletableFuture<Object> responseFuture = zmqClient.sendMessage(requestDto, ChallengeRequestDto.class);

        // Log response once received
        responseFuture.thenAccept(response -> {
            log.info("Received response from Challenge: {}", response);
        });

        return responseFuture;

    }
}
