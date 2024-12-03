package com.itachallenge.score.service;

import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface CodeProcessingManager {

    ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest);

}
