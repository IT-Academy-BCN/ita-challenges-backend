package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;

public interface IScoreService {
    TestingValuesResponseDto getTestParams(String challengeId);
}
