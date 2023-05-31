package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dtos.ChallengeDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChallengeService {

    public Mono<ChallengeDto> getChallengeId(UUID id); //Collect a challenge by ID.

    public boolean isValidUUID(String id); //Validates the challenge ID by comparing it with an ID of type UUID.

}
