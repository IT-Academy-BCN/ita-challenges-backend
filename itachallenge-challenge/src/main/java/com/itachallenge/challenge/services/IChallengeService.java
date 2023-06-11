package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dtos.ChallengeDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IChallengeService {

    public Mono<ChallengeDto> getChallengeId(UUID id);
    public boolean isValidUUID(String id);
}
