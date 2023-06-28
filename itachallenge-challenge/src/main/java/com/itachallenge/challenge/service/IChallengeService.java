package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    public Mono<Flux<ChallengeDto>> getChallengeId(UUID id) ;
    public boolean isValidUUID(String id);

}
