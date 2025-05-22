package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.SolvedDto;
import com.itachallenge.challenge.service.IChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ChallengeSolvedControllerTest {

    private IChallengeService challengeService;
    private ChallengeSolvedController controller;

    @BeforeEach
    void setUp() {
        challengeService = mock(IChallengeService.class);
        controller = new ChallengeSolvedController(challengeService);
    }

    @Test
    void testAddChallengeToSolved_ReturnsCreated_WhenSolvedIsTrue() {

        String challengeId = "123";
        SolvedDto solvedDto = new SolvedDto();
        solvedDto.setSolved(true);

        when(challengeService.addChallengeToSolved(challengeId))
                .thenReturn(Mono.just(solvedDto));


        StepVerifier.create(controller.addChallengeToSolved(challengeId))
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertTrue(response.getBody().isSolved());
                })
                .verifyComplete();
    }

    @Test
    void testAddChallengeToSolved_ReturnsOk_WhenSolvedIsFalse() {

        String challengeId = "456";
        SolvedDto solvedDto = new SolvedDto();
        solvedDto.setSolved(false);

        when(challengeService.addChallengeToSolved(challengeId))
                .thenReturn(Mono.just(solvedDto));


        StepVerifier.create(controller.addChallengeToSolved(challengeId))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    assertNotNull(response.getBody());
                    assertFalse(response.getBody().isSolved());
                })
                .verifyComplete();
    }

    @Test
    void testAddChallengeToSolved_PropagatesError() {

        String challengeId = "999";
        when(challengeService.addChallengeToSolved(challengeId))
                .thenReturn(Mono.error(new RuntimeException("Test error")));


        StepVerifier.create(controller.addChallengeToSolved(challengeId))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Test error")
                )
                .verify();
    }
}
