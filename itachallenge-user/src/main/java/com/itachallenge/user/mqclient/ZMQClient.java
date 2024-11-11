package com.itachallenge.user.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.user.helper.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.ZMQException;

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
            if (message == null) {
                throw new IllegalArgumentException("Message cannot be null");
            }

            byte[] request = serializeMessage(message);

            Optional<Object> response = Optional.empty();
            try (ZMQ.Socket socket = context.createSocket(SocketType.REQ)) {
                socket.connect(SOCKET_ADDRESS);
                socket.send(request, 0);

                byte[] reply = socket.recv(0);
                if (reply == null) {
                    throw new ZMQException("Received null reply from ZeroMQ", ZMQ.Error.ETERM.getCode());
                }

                response = deserializeMessage(reply, clazz);
            } catch (ZMQException e) {
                throw new CompletionException(e);
                }

            return response.orElse(null);

        }, executorService);
    }

    private byte[] serializeMessage(Object message) {
        try {
            return objectSerializer.serialize(message);
        } catch (JsonProcessingException e) {
            throw new CompletionException(e);
        }
    }

    private <T> Optional<T> deserializeMessage(byte[] data, Class<T> clazz) {
        try {
            return Optional.of(objectSerializer.deserialize(data, clazz));
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }
}