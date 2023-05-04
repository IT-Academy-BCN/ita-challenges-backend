package com.itachallenge.challenge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helper.ResourceHelper;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ChallengeServiceTest {


    @Autowired
    ChallengeService challengeService;

    private static ResourceHelper resourceHelper;

    @DisplayName("Get All Challenges Test")
    @Test
    void getAllChallengesTest() throws IOException {

        Mono<String> result = challengeService.getAllChallenges();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldGetAllChallenges() throws IOException {

        InputStream inputStream = new ClassPathResource("data-challenge.json").getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();

        String result = challengeService.getAllChallenges().block();

        JsonNode jsonNode = objectMapper.readTree(result);

        assertFalse(jsonNode.isEmpty());
        assertTrue(jsonNode.size() > 0);



    }
}




