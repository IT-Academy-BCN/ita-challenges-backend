package com.itachallenge.challenge.service;

import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    public Mono<?> getChallengeId(UUID id);
    public boolean isValidUUID(String id);

}
