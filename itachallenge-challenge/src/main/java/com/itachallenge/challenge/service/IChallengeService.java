package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.RelatedDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeId(UUID id);
    boolean isValidUUID(String id);
    boolean removeResourcesByUuid(UUID idResource);
    Flux<ChallengeDto> getRelatedChallenge(String challengeId);


}
