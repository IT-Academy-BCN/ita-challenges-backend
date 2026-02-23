package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryResponseDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PointsService {

    Mono<PointsHistoryResponseDto> getUserPointsHistory(UUID userId);
}

