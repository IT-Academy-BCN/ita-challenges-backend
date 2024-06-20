package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Optional;

@Component
public class ZMQServer {

    private final ZContext context;
    private final String SOCKET_ADDRESS;
    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);

    @Autowired
    ObjectSerializer objectSerializer;

    public ZMQServer(ZContext context, @Value("${zeromq.socket.address}") String socketAddress){
        this.context = context;
        this.SOCKET_ADDRESS = socketAddress;
    }

    @PostConstruct
    public void init() {
        log.info("Starting ZMQ Server");
        new Thread(this::run).start();
    }

    public void run(){
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5555");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] reply = socket.recv(0);

                Optional<Object> request = Optional.empty();
                try {
                    request = Optional.of(objectSerializer.deserialize(reply, ScoreRequestDto.class));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }

                log.info("Received: [" + ((ScoreRequestDto)request.get()).getChallengeId() + "]");
                ScoreResponseDto dto = new ScoreResponseDto();
                dto.setPercent(99);

                Optional<byte[]> response = Optional.empty();
                try {
                    response = Optional.of(objectSerializer.serialize(dto));
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage());
                }

                socket.send(response.orElse(new byte[0]), 0);
            }
        }
    }

}
