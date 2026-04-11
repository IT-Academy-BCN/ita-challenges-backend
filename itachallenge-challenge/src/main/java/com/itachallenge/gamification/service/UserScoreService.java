package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import com.itachallenge.gamification.enums.ActivityType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreService {

    Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId);

    Mono<Void> registerPoints(UUID userId, String username, ActivityType type, UUID challengeId);
}


