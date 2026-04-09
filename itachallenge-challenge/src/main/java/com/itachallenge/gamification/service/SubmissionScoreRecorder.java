package com.itachallenge.gamification.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SubmissionScoreRecorder {

    Mono<Void> recordCompletedSubmissionScore(UUID userId, UUID challengeId);
}
