package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    Mono<?> getChallengeId(UUID id);
    boolean isValidUUID(String id);
    boolean removeResourcesById(UUID idResource);

}
