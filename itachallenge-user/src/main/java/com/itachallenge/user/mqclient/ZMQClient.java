package com.itachallenge.user.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.user.helper.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.*;

@Component
public class ZMQClient {

    private final ZContext context;
    private final String SOCKET_ADDRESS;
    private final ObjectSerializer objectSerializer;
    private static final Logger log = LoggerFactory.getLogger(ZMQClient.class);

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ZMQClient(ZContext context, @Value("${zeromq.socket.address}") String socketAddress, ObjectSerializer objectSerializer) {
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
        this.objectSerializer = objectSerializer;
    }

    public CompletableFuture<Object> sendMessage(Object message, Class clazz) {

        return CompletableFuture.supplyAsync(() -> {

            Optional<Object> response = Optional.empty();

            try (ZMQ.Socket socket = context.createSocket(ZMQ.REQ)) {
                socket.connect(SOCKET_ADDRESS);

                Optional<byte[]> request = Optional.empty();
                try {
                    request = Optional.of(objectSerializer.serialize(message));
                } catch (JsonProcessingException jpe) {
                    log.error("Error serializing message: {}", jpe.getMessage(), jpe);
                }
                socket.send(request.orElse(new byte[0]), 0);

                byte[] reply = socket.recv(0);
                if (reply == null) {
                    log.error("Received null reply from ZeroMQ");
                }

                try {
                    response = Optional.of(objectSerializer.deserialize(reply, clazz));
                } catch (IOException e) {
                    log.error("Error deserializing reply: {}", e.getMessage(), e);
                }

            } catch (Exception e) {
                log.error("Error in ZMQClient sendMessage: {}", e.getMessage(), e);

            }

            return response.orElse(null);

        }, executorService);
    }
}