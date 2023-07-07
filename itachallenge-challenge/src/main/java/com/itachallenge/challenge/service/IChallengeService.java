package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import com.itachallenge.challenge.dto.RelatedDto;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeId(UUID id);

    boolean isValidUUID(String id);

    boolean removeResourcesByUuid(UUID idResource);
    
    Mono<List<RelatedDto>> getRelatedChallenge(String id);

}
