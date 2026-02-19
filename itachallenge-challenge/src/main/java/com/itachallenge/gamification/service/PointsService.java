package com.itachallenge.gamification.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PointsService {

    Mono<Void> recordPoints(UUID userId, UUID challengeId, int points);
}
