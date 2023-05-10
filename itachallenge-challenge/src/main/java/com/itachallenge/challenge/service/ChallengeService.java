package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class ChallengeService {

    private String JsonResourcePath = "/data-challenge.json";

    public Mono<String> getAllChallenges() {

        ResourceHelper helper = new ResourceHelper();

        String JsonResource = helper.readResourceAsString(JsonResourcePath);

        return Mono.just(JsonResource);

    }

}
