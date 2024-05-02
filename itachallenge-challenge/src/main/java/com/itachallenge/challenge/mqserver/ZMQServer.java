package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.document.TestingValueDocument;
import com.itachallenge.challenge.dto.TestingValueDto;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.helper.ObjectSerializer;
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

import java.io.IOException;
import java.util.Optional;

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
    protected IChallengeService challengeService;

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
            ChallengeRequestDto requestDto;
            TestingValuesResponseDto responseDto;
            Optional<Object> request;
            Optional<byte[]> response;
            byte[] reply;

            socket.bind(SOCKET_ADDRESS);

            while (!Thread.currentThread().isInterrupted() && isRunning) {
                request = Optional.empty();
                response = Optional.empty();
                reply = socket.recv(0);

                try {
                    request = Optional.of(objectSerializer.deserialize(reply, ChallengeRequestDto.class));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                if(request.isPresent()) {
                    requestDto = (ChallengeRequestDto) request.get();
                    log.info("Received: [" + requestDto.getChallengeId() + "]");

                    responseDto = challengeService.getTestingParamsByChallengeUuid(requestDto.getChallengeId()).block();

                    try {
                        response = Optional.of(objectSerializer.serialize(responseDto));
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
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

