package com.itachallenge.challenge.service;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;


@Service
public class ChallengeService {

    private String JsonResourcePath = "/data-challenge.json";

    public Mono<String> getAllChallenges() throws IOException {

        ResourceHelper helper = new ResourceHelper(JsonResourcePath);

        String JsonResource = helper.readResourceAsString();

        return Mono.just(JsonResource);

    }

}
