package com.itachallenge.challenge.services;

import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import com.itachallenge.challenge.dtos.RelatedDto;

public interface IChallengeService {

    public Mono<?> getChallengeId(UUID id);
    public boolean isValidUUID(String id);
    public Set<RelatedDto> getRelatedChallenge(UUID challenge_id);

}
