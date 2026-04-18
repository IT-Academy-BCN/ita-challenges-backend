package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.enums.ActivityType;
import com.itachallenge.challenge.dto.gamification.ScoresHistoryResponseDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreService {

    Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId);

    Mono<Void> registerPoints(UUID userId, ActivityType type, UUID challengeId);

    Mono<Integer> assignPoints(UUID userId, ActivityType activityType);

    Mono<ScoresHistoryResponseDto> getUserScoresHistoryChart(UUID userId);

}

