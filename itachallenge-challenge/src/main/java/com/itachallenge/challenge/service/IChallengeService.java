package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import com.itachallenge.challenge.dto.RelatedDto;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeId(UUID id);

    boolean isValidUUID(String id);

    boolean removeResourcesByUuid(UUID idResource);
    
    Flux<RelatedDto> getRelatedChallenge(String challengeId);

}
