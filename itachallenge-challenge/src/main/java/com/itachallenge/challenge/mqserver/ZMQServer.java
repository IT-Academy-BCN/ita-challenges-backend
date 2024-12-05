package com.itachallenge.challenge.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.challenge.helper.ObjectSerializer;
import com.itachallenge.challenge.service.IChallengeService;
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
import java.util.UUID;

//@Component
//public class ZMQServer {
//    private final ZContext context;
//    private final String SOCKET_ADDRESS;
//    private static final Logger log = LoggerFactory.getLogger(ZMQServer.class);
//
//    @Autowired
//    ObjectSerializer objectSerializer;
//
//    @Autowired
//    IChallengeService challengeService;
//
//    public ZMQServer(ZContext context, @Value("${zeromq.server.socket.address}") String socketAddress){
//        this.context = context;
//        this.SOCKET_ADDRESS = socketAddress;
//    }
//
//    @PostConstruct
//    public void init() {
//        log.info("Starting ZMQ Server");
//        new Thread(this::run).start();
//    }
//
//    public void run(){
//        try (ZContext context = new ZContext()) {
//            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
//            socket.bind(SOCKET_ADDRESS);
//
//            while (!Thread.currentThread().isInterrupted()) {
//                byte[] reply = socket.recv(0);
//
//                Optional<Object> request = Optional.empty();
//                try {
//                    request = Optional.of(objectSerializer.deserialize(reply, ChallengeRequestDto.class));
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                }
//
//                UUID challengeId = ((ChallengeRequestDto)request.get()).getChallengeId();
//
//                log.info("Received: [" + challengeId + "]");
//                //ahora tendria que hacer una call a service para buscar en la base de datos la info de este challenge!!
//
//                TestingValuesResponseDto challenge = challengeService.getChallengeById(challengeId.toString()).map(
//                        challengeDto -> {
//
//                        }
//                );
//
//                StatisticsResponseDto dto = new StatisticsResponseDto();
//                dto.setPercent(99);
//
//                Optional<byte[]> response = Optional.empty();
//                try {
//                    response = Optional.of(objectSerializer.serialize(dto));
//                } catch (JsonProcessingException e) {
//                    log.error(e.getMessage());
//                }
//
//                socket.send(response.orElse(new byte[0]), 0);
//            }
//        }
//    }
//}
