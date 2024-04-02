package com.itachallenge.challenge.mqclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.helper.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static final Logger log = LoggerFactory.getLogger(ZMQClient.class);

    @Autowired
    ObjectSerializer objectSerializer;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ZMQClient(ZContext context, @Value("${zeromq.socket.address}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS= socketAddress;

        //luego borrar este log (ahora es para verificar que se inyecta la dirección del socket)
        log.info("Socket Address: {}", socketAddress);


    }

    public CompletableFuture<Object> sendMessage(Object message, Class clazz){
        CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {

            ZContext context = new ZContext();
                ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
                socket.connect(SOCKET_ADDRESS);

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
/*CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
            try (ZContext context = new ZContext()) {
                ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
                socket.connect(SOCKET_ADDRESS);

                byte[] serializedMessage = objectSerializer.serialize(message);
                socket.send(serializedMessage, 0);

                byte[] reply = socket.recv(0);
                Object deserializedResponse = objectSerializer.deserialize(reply, clazz);
                return deserializedResponse;
            } catch (Exception e) {
                log.error("Error sending message: {}", e.getMessage());
                return null;
            }
        }, executorService);

        return future;*/

}