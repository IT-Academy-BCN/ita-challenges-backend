package com.itachallenge.score.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.helper.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



@Component
public class ZMQScoreClient {
    private final ZContext context;
    private final String CLIENT_SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(ZMQScoreClient.class);

    @Autowired
    ObjectSerializer objectSerializer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ZMQScoreClient(ZContext context, @Value("${zeromq.socket.address_client}") String socketAddress){
        this.context = context;
        this.CLIENT_SOCKET_ADDRESS = socketAddress;
    }

    public CompletableFuture<Object> sendMessage(Object message, Class clazz){

        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {

            ZContext context = new ZContext();
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect(CLIENT_SOCKET_ADDRESS);

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
        return future;
    }
}
