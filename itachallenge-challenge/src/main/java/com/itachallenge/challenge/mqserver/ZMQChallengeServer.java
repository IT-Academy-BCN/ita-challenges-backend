package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.dto.TestingValueDto;
import com.itachallenge.challenge.dto.zmq.TestingValuesRequestDto;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.service.IChallengeService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class ZMQChallengeServer {
    private final ZContext context;
    private final String SERVER_SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(ZMQChallengeServer.class);

    @Autowired
    IChallengeService challengeService;

    public ZMQChallengeServer(ZContext context, @Value("${zeromq.socket.address_server}") String socketAddress){
        this.context = context;
        this.SERVER_SOCKET_ADDRESS = socketAddress;
    }

    @PostConstruct
    public void init() {
        log.info("Starting ZMQ Server");
        new Thread(this::run).start();
    }

    public void run(){
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind(SERVER_SOCKET_ADDRESS);

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);

                Optional<Object> request = Optional.empty();
                try {
                    request = Optional.of(ObjectSerializer.deserialize(reply, TestingValuesRequestDto.class));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                TestingValuesRequestDto dto = (TestingValuesRequestDto)request.get();
                log.info("Received: [" + dto.getChallengeId() + ", "
                        + dto.getLanguageId() + "]");

                CompletableFuture<List<TestingValueDto>> future = new CompletableFuture<>();
                challengeService.getTestingParamsByChallengeIdAndLanguageId(
                        dto.getChallengeId().toString(), dto.getLanguageId().toString())
                        .subscribe(response -> {
                                    List<TestingValueDto> testParams = (List<TestingValueDto>) response.get("test_params");
                                    future.complete(testParams);
                                },
                                error ->
                                System.err.println("Error: " + error.getMessage()));

                Optional<byte[]> response = Optional.empty();
                try {
                    List<TestingValueDto> testParams = future.get();
                    response = Optional.of(ObjectSerializer.serialize(testParams));
                } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
                    log.error(e.getMessage());
                }

                socket.send(response.orElse(new byte[0]), 0);
            }
        }
    }
}