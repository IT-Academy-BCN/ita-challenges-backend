package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import com.itachallenge.challenge.dto.RelatedDto;

public interface IChallengeService {

    public Mono<?> getChallengeId(UUID id);
    public boolean isValidUUID(String id);
    Mono<List<RelatedDto>> getRelatedChallengePaginated(String id, int page, int size);

}
