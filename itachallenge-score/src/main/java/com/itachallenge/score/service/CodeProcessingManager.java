package com.itachallenge.score.service;

import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.util.ExecutionResult;
import org.springframework.http.ResponseEntity;

public interface CodeProcessingManager {

    ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest);

    int calculateScore(ExecutionResult executionResult, String resultExpected);

}
