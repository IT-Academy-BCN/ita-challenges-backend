package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    public Mono<ChallengeDto> getChallengeId(UUID id) ;
    public boolean isValidUUID(String id);

}
