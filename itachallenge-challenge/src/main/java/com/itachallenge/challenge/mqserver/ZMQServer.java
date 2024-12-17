package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.dto.ChallengeTestingValuesDto;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.service.IChallengeService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class ZMQServer {
    private final ZContext context;
    private final IChallengeService challengeService;
    private final String socketAddress;
    private final Thread serverThread;
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);
    private boolean running = true;

    public ZMQServer(ZContext context, IChallengeService challengeService,
                     @Value("${zeromq.server.socket.address}") String socketAddress){
        this.context = context;
        this.challengeService = challengeService;
        this.socketAddress = socketAddress;
        this.serverThread = new Thread(this::run, "ZMQServer-Thread");
    }

    @PostConstruct
    public void init() {
        log.info("Starting ZMQ Server");
        serverThread.start();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down ZMQ Server");
        this.running = false;
        serverThread.interrupt();
        context.close();
    }

    public void run(){
        try (ZMQ.Socket socket = context.createSocket(SocketType.REP)) {
            socket.bind(socketAddress);
            while (running && !Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);
                Optional<ChallengeRequestDto> request = deserializeRequest(reply);

                UUID challengeId = request.map(ChallengeRequestDto::getChallengeId)
                        .orElseThrow(() -> new IllegalStateException("Request is empty"));

                //log.info("Received challenge id: [{}]", challengeId);      //TODO delete line

                challengeService.getTestingParamsByChallengeId(challengeId.toString())
                        .flatMap(this::serializeResponse)
                        .doOnError(e -> log.error("Error during service call or processing: {}", e.getMessage()))
                        .doOnNext(serializedData -> socket.send(serializedData, 0))
                        .subscribe();
            }
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    public Optional<ChallengeRequestDto> deserializeRequest(byte[] reply) {
        try {
            return Optional.ofNullable(ObjectSerializer.deserialize(reply, ChallengeRequestDto.class));
        } catch (IOException e) {
            log.error("Deserialization failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Mono<byte[]> serializeResponse(ChallengeTestingValuesDto challenge) {
        try {
            if (challenge == null) {
                return Mono.just(new byte[0]);
            }
            byte[] serializedData = ObjectSerializer.serialize(challenge);
            return Mono.just(serializedData);
        } catch (JsonProcessingException e) {
            log.error("Serialization error: {}", e.getMessage());
            return Mono.just(new byte[0]);
        }
    }
}