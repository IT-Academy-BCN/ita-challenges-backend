package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);

    Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId);
    Mono<Boolean> removeChallengeFromFavorites(String userId, String challengeId);

    Mono<Boolean> addChallengeToSolved(String userId, String challengeId);

    Mono<Boolean> removeChallengeFromBookmarks(String userId, String challengeId);
}
