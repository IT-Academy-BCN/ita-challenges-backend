package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);
    Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId);
}
