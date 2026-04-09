package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreService {

    Mono<ScoresHistoryResponseDto> getUserScoresHistoryChart(UUID userId);

}

