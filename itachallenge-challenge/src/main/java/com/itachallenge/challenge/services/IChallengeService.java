package com.itachallenge.challenge.services;


import java.util.Set;
import java.util.UUID;

import com.itachallenge.challenge.dto.RelatedDto;

import reactor.core.publisher.Mono;

public interface IChallengeService {

 
    public Mono<Set<RelatedDto>> getRelatedChallenge(UUID challengeId);

}
