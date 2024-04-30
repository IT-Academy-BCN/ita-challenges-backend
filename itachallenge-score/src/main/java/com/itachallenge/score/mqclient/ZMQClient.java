package com.itachallenge.score.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.helper.ObjectSerializer;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ZMQClient {

    private final ZContext context;
    private final ZMQ.Socket socket;
    private final String SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(ZMQClient.class);

    @Autowired
    private ObjectSerializer objectSerializer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ZMQClient(ZContext context, @Value("${zeromq.socket.address.challenge}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
        this.socket = context.createSocket(SocketType.REQ);
        socket.connect(SOCKET_ADDRESS);
    }

    public CompletableFuture<Object> sendMessage(Object message, Class clazz){

        return CompletableFuture.supplyAsync(() -> {

            Optional<byte[]> request = Optional.empty();
            try {
                request = Optional.of(objectSerializer.serialize(message));
            }catch (JsonProcessingException jpe){
                log.error(jpe.getMessage());
            }
            socket.send(request.orElse(new byte[0]), 0);

            byte[] reply = socket.recv(0);
            Optional<Object> response = Optional.empty();
            try {
                response = Optional.of(objectSerializer.deserialize(reply, clazz));
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return response.orElse(null);

        }, executorService);
    }

    @PreDestroy
    public void cleanup() {
        context.close();
    }
}
