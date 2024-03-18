package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.StatisticsResponseDto;
import com.itachallenge.challenge.mqclient.ZMQClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
@Service
public class ZMQClientServiceImpl implements IZMQClientService {
    private static final Logger log = LoggerFactory.getLogger(ZMQClientServiceImpl.class);
    private ZMQClient zmqClient;

    @Autowired
    public ZMQClientServiceImpl(ZMQClient zmqClient) {
        this.zmqClient = zmqClient;
    }

    @Override
    public void requestUserData() {
        ChallengeRequestDto challengeRequestDto = new ChallengeRequestDto();

        CompletableFuture<Object> future = zmqClient.sendMessage(challengeRequestDto, StatisticsResponseDto.class);

        // Trabajar en otras tareas mientras se espera la respuesta
        future.thenAccept(response -> {
            // Aquí maneja la respuesta recibida
            if (response instanceof StatisticsResponseDto) {
                StatisticsResponseDto statisticsResponseDto = (StatisticsResponseDto) response;
                // Haz algo con la respuesta recibida, por ejemplo:
                log.info("Percentage: {}", statisticsResponseDto.getPercent());
                log.info("Bookmarks: {}", statisticsResponseDto.getBookmarks());
            } else {
                log.error("Received unexpected response: {}", response);
            }
        });

    }
}
