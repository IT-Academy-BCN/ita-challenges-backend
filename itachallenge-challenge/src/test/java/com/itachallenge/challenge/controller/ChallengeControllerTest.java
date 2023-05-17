package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.services.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeController.class, excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
public class ChallengeControllerTest {
    @Mock
    private ChallengeService challengeService;
    @InjectMocks
    private ChallengeController challengeController;
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }

    @Test
    void testGetOneChallengeWithValidUUID() {
        String validId = "valid-id";
        Challenge challenge = new Challenge();

        when(challengeService.isValidUUID(validId)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(validId))).thenReturn(Mono.just(challenge));

        ResponseEntity<Mono<Challenge>> response = challengeController.getOneChallenge(validId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(challenge, response.getBody().block());

        verify(challengeService, times(1)).isValidUUID(validId);
        verify(challengeService, times(1)).getChallengeId(UUID.fromString(validId));
    }

}



