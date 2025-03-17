package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<Boolean> addChallengeToFavorites(String userId, String challengeId);

}
