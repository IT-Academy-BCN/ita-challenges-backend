package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface IChallengeService {

    Mono<ChallengeDto> getChallengeId(UUID id);
    boolean isValidUUID(String id);
    boolean removeResourcesByUuid(UUID idResource);

}
