package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dto.ChallengeDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    Mono<?> getChallengeId(UUID id);
    boolean isValidUUID(String id);
    Flux<ChallengeDto> getChallenges ();

}
