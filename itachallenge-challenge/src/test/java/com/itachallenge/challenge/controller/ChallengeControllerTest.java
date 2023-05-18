package com.itachallenge.challenge.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.services.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class, excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
public class ChallengeControllerTest {


    @Autowired
    private WebTestClient webTestClient;
    @Mock
    private ChallengeService challengeService;

    private final String CONTROLLER_BASE_URL = "/itachallenge/api/v1/challenge";
    final String URI_TEST = "/test";

/*    @InjectMocks
    private ChallengeController challengeController;*/

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {

        webTestClient.get()
                .uri(CONTROLLER_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s.toString(), equalTo("Hello from ITA Challenge!!!"));

        assertEquals(1, 1);
    }

/*    @Test
    void testGetOneChallengeValidUUID() {
        String validId = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        Challenge challenge = new Challenge();

        when(challengeService.isValidUUID(validId)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(validId))).thenReturn(Mono.just(challenge));

        ResponseEntity<Mono<Challenge>> response = challengeController.getOneChallenge(validId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(challenge, response.getBody().block());

    }*/

}



