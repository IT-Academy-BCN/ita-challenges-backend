package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import reactor.core.publisher.Mono;

public interface IScoreService {
    Mono<TestingValuesResponseDto> getTestParams(String challengeId);
}
