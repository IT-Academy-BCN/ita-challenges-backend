package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.document.TestingValueDocument;
import com.itachallenge.challenge.dto.TestingValueDto;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.service.IChallengeService;
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
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ZMQServer {

    private final ZContext context;
    private final String SOCKET_ADDRESS;

    private volatile boolean isRunning;
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);

    @Autowired
    private ObjectSerializer objectSerializer;

    @Autowired
    private DocumentToDtoConverter<TestingValueDocument, TestingValueDto> converter;

    @Autowired
    private IChallengeService challengeService;

    public ZMQServer(ZContext context, @Value("${zeromq.socket.address.server}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
        isRunning = true;
    }

    @PostConstruct
    public void init() {
        log.info("Starting ZMQ Server");
        new Thread(this::run).start();
    }

    public void run(){
        try (ZMQ.Socket socket = context.createSocket(SocketType.REP)) {
            socket.bind(SOCKET_ADDRESS);

            while (!Thread.currentThread().isInterrupted() && isRunning) {
                byte[] reply = socket.recv(0);

                Optional<Object> request = Optional.empty();
                try {
                    request = Optional.of(objectSerializer.deserialize(reply, ChallengeRequestDto.class));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                ChallengeRequestDto requestDto = (ChallengeRequestDto) request.get();

                log.info("Received: [" + requestDto.getChallengeId() + "]");

                TestingValuesResponseDto responseDto = challengeService.getTestingParamsByChallengeUuid(requestDto.getChallengeId()).block();

                Optional<byte[]> response = Optional.empty();
                try {
                    response = Optional.of(objectSerializer.serialize(responseDto));
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }

                socket.send(response.orElse(new byte[0]), 0);
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        context.close();
    }

    public void stop() {
        isRunning = false;
    }
}

