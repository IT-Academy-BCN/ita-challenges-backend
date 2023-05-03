package com.itachallenge.challenge.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeServiceTest {

    @Test
    void shouldGetAllChallenges() throws IOException {

        InputStream inputStream = new ClassPathResource("data-challenge.json").getInputStream();

        ObjectMapper objectMapper = new ObjectMapper();

        ChallengeService challengeService = new ChallengeService();

        String result = challengeService.getAllChallenges(0, 10).block();

        JsonNode jsonNode = objectMapper.readTree(result);

        assertFalse(jsonNode.isEmpty());
        assertTrue(jsonNode.size() > 0);



    }
}




