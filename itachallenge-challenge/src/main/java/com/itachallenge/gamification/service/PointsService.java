package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.PointsHistoryDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PointsService {

  Mono<PointsHistoryDto> getUserPointsHistory(UUID userId);
}

