package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.PropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final PropertiesConfig config;

    public Mono<String> getFiltersInfo(){
        return Mono.just(config.getFiltersData());
    }

    public Mono<String> getSortInfo(){
        return Mono.just(config.getSortData());
    }
}
