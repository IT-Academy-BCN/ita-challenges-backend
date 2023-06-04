package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChallengeService {


    public Flux<ChallengeDto> getAllRelatedsByUuid(UUID id);

    public Mono<GenericResultDto<ChallengeDto>> getRelateds(UUID id, int offset, int limit);
}
