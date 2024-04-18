package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.score.dto.zmq.ChallengeRequestDto;
import com.itachallenge.score.mqclient.ZMQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class ScoreServiceImpl implements IScoreService{
    @Autowired
    ZMQClient zmqClient;

    public TestingValuesResponseDto getTestParams(String challengeId) {
        ChallengeRequestDto challengeRequestDto = ChallengeRequestDto.builder()
                .challengeId(UUID.fromString(challengeId))
                .build();
        //TODO fix try-catch
        try {
            return (TestingValuesResponseDto) zmqClient.sendMessage(challengeRequestDto, TestingValuesResponseDto.class).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
