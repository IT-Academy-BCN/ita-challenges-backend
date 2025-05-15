package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.SolvedDto;
import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class ChallengeSolvedControllerTest {

    @Mock
    private IChallengeService challengeService;

    @Mock
    private IJwtService jwtService;

    @InjectMocks
    private ChallengeSolvedController challengeSolvedController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addChallengeToSolved_Success() {
        String challengeId = "123";
        String authHeader = "Bearer validToken";
        String userId = "user123";
        SolvedDto solvedDto = new SolvedDto();

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(challengeService.addChallengeToSolved(challengeId)).thenReturn(Mono.just(solvedDto));

        Mono<ResponseEntity<SolvedDto>> result = challengeSolvedController.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    assertEquals(solvedDto, response.getBody());
                })
                .verifyComplete();

        verify(challengeService, times(1)).addChallengeToSolved(challengeId);
    }

    @Test
    void addChallengeToSolved_InvalidAuthHeader() {
        String challengeId = "123";
        String authHeader = "invalidToken";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException("Invalid token"));

        Mono<ResponseEntity<SolvedDto>> result = challengeSolvedController.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().equals("Invalid token"))
                .verify();

        verify(jwtService, times(1)).getUserUuIdFromAuthenticationHeader(authHeader);
        verifyNoInteractions(challengeService);
    }

    @Test
    void addChallengeToSolved_ServiceError() {
        String challengeId = "123";
        String authHeader = "Bearer validToken";
        String userId = "user123";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(challengeService.addChallengeToSolved(challengeId)).thenReturn(Mono.error(new RuntimeException("Service error")));

        Mono<ResponseEntity<SolvedDto>> result = challengeSolvedController.addChallengeToSolved(challengeId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Service error"))
                .verify();

        verify(challengeService, times(1)).addChallengeToSolved(challengeId);
    }
}