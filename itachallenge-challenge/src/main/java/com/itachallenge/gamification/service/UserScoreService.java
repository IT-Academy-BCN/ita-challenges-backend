package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.challenge.dto.gamification.ScoresHistoryChartDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreService {

    Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId);
    Mono<ScoresHistoryChartDto> getUserScoresHistoryChart(UUID userId);

}

