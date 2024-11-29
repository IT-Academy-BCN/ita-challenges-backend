package com.itachallenge.score.service;

import com.github.dockerjava.api.DockerClient;
import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.util.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ScoreChallengeService implements CodeProcessingManager{

    private final DockerClient dockerClient;

    @Override
    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {
        return null;
    }

    @Override
    public int calculateScore(ExecutionResult executionResult, String resultExpected) {
        return 0;
    }

}
