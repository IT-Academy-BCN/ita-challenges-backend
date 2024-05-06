package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.score.dto.zmq.ChallengeRequestDto;
import com.itachallenge.score.mqclient.ZMQClient;
import org.hibernate.validator.internal.constraintvalidators.hv.UUIDValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ScoreServiceImpl implements IScoreService{
    @Autowired
    ZMQClient zmqClient;
    //TODO Proof of concept. Missing proper validations
    public Mono<TestingValuesResponseDto> getTestParams(String challengeId) {
        UUIDValidator uuidValidator = new UUIDValidator();
        if (!uuidValidator.isValid(challengeId, null)) {
            return Mono.error(new IllegalArgumentException("Invalid UUID"));
        }

        ChallengeRequestDto challengeRequestDto = ChallengeRequestDto.builder()
                .challengeId(UUID.fromString(challengeId))
                .build();
        return Mono.fromFuture(zmqClient
                .sendMessage(challengeRequestDto, TestingValuesResponseDto.class)
                .thenApply(TestingValuesResponseDto.class::cast));
    }
}
