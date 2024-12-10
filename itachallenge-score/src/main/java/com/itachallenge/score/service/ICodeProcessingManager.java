package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import org.springframework.http.ResponseEntity;

public interface ICodeProcessingManager {
    ResponseEntity<ScoreResponseDto> processCode(ScoreRequestDto scoreRequest);
}
