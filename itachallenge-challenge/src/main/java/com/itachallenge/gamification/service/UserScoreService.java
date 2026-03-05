package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserScoreService {

    Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId);
}

