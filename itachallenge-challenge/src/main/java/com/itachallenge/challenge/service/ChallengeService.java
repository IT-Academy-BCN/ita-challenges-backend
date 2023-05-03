package com.itachallenge.challenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ChallengeService {

    public Mono<String> getAllChallenges(int offset, int limit) throws IOException {

        try {
            InputStream inputStream = getClass().getResourceAsStream("/data-challenge.json");
            ObjectMapper objectMapper = new ObjectMapper();
            String challenges = String.valueOf(objectMapper.readTree(inputStream));

            return Mono.just(challenges);
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

}
