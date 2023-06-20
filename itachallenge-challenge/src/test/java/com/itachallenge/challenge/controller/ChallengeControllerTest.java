package com.itachallenge.challenge.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ChallengeControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    @Test
    @DisplayName("Test EndPoint: test")
    void TestEndPoint_test(){
        final String URI_TEST = "/test";
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s, equalTo("Hello from ITA Challenge!!!"));
    }
    
    @Test
    @DisplayName("Test EndPoint: related")
    void ChallengeRelatedTest (){
        final String URI_RELATED = "/{dcacb291-b4aa-4029-8e9b-284c8ca80296}/related";
        
    	Set<UUID> challengerelated= new HashSet<UUID>();
		challengerelated.add(UUID.fromString("40728c9c-a557-4d12-bf8f-3747d0924197"));
		challengerelated.add(UUID.fromString("1aeb27aa-7d7d-46c7-b5b8-4a2354966cd0"));
		challengerelated.add(UUID.fromString("5f71e51d-1e3e-44a2-bc97-158021f1a344"));
		
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_RELATED)
                .accept(MediaType.ALL)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Set.class)
                .value(s -> s, equalTo(challengerelated));
    }
    
}

