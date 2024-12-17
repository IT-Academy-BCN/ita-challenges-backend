package com.itachallenge.score.mqclient;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.itachallenge.score.dto.zmq.TestParamsRequestDto;
import com.itachallenge.score.dto.zmq.TestParamsResponseDto;
import com.itachallenge.score.util.FileUtil;
import com.itachallenge.score.util.ObjectSerializer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ZMQClient {

    private final ZContext context;
    private static final Logger log = LoggerFactory.getLogger(ZMQClient.class);
    private final String SOCKET_ADDRESS;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ObjectSerializer objectSerializer;
    private final FileUtil fileUtil;

    @Autowired
    public ZMQClient(ZContext context, @Value("${zeromq.socket.address}") String socketAddress,
                     FileUtil fileUtil, ObjectSerializer objectSerializer) {
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
        this.fileUtil = fileUtil;
        this.objectSerializer = objectSerializer;
    }


    public CompletableFuture<Void> requestTestParams(UUID challengeId,  UUID solutionId) {
        return CompletableFuture.runAsync(() -> {
            try (ZMQ.Socket socket = context.createSocket(SocketType.REQ)){
                socket.connect(SOCKET_ADDRESS);

                TestParamsRequestDto request = TestParamsRequestDto.builder()
                        .uuidChallenge(challengeId)
                        .build();

                byte[] serializedRequest = objectSerializer.serialize(request);
                socket.send(serializedRequest, 0);

                byte[] replyBytes = socket.recv(0);
                if (replyBytes == null || replyBytes.length == 0) {
                    throw new RuntimeException("Empty response from Challenge Micro server");
                }

                TestParamsResponseDto response = objectSerializer.deserialize(replyBytes, TestParamsResponseDto.class);

                List<String> paramsList = response.toParamList();
                fileUtil.createTestParamsFile(paramsList, solutionId);

            } catch (Exception e) {
                log.error("Error interacting with the Challenge Micro server", e);
            }
        }, executorService);
    }
}


