package com.itachallenge.challenge.controller;

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

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;

import java.util.UUID;

//@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
//@RunWith(MockitoJUnitRunner.class)
//@WebFluxTest(controllers = ChallengeController.class, excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@SpringBootTest
public class ChallengeControllerTest {

    @Mock
    //@MockBean
    private ChallengeService challengeService;
    @InjectMocks
    //@MockBean
    private ChallengeController challengeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }

    @Test
    void testGetOneChallengeValidUUID() {
        String validId = "dcacb291-b4aa-4029-8e9b-284c8ca80296";
        Challenge challenge = new Challenge();

        when(challengeService.isValidUUID(validId)).thenReturn(true);
        when(challengeService.getChallengeId(UUID.fromString(validId))).thenReturn(Mono.just(challenge));

        ResponseEntity<Mono<Challenge>> response = challengeController.getOneChallenge(validId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(challenge, response.getBody().block());

    }

}



