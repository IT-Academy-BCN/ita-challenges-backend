package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.*;
import reactor.core.publisher.Mono;



public interface IResourceService {
    Mono<ResourceDto> createResource(ResourceDto resourceDto);

}

