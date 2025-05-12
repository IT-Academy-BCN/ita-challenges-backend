package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface IResourceService {
    Mono<ResourceDto> createResource(ResourceDto resourceDto);

    Flux<ResourceDto> getResourcesByChallengeId(UUID challengeId);
}

