package com.itachallenge.challenge.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IChallengeService {

    public Mono<?> getChallengeId(UUID id);
    public boolean isValidUUID(String id);
    public Mono<List<UUID>> getRelatedChallengePaginated(String id, int page, int size);

}
