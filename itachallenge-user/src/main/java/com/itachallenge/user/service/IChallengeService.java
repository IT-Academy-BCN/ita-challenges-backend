package com.itachallenge.user.service;

import reactor.core.publisher.Mono;

public interface IChallengeService {
    
    Mono<Boolean> addChallengeToSolved(String challengeId);

}
