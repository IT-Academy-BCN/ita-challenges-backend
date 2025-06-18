package com.itachallenge.user.service;

import com.itachallenge.user.dto.SolvedDto;
import reactor.core.publisher.Mono;

public interface IChallengeService {

    Mono<SolvedDto> addChallengeToSolved(String challengeId);

}