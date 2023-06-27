package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.ChallengeDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
class ChallengeServiceImpTest {
    //VARIABLES
    private final static UUID VALID_ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
    private final static String INVALID_ID = "123456789";

    @InjectMocks
    private ChallengeServiceImp challengeService;
    @MockBean
    private ChallengeDto challengeDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChallengeId() {
        ChallengeDto expectedDto = new ChallengeDto();
        expectedDto.setChallengeId(VALID_ID);

        when(challengeDto.getChallengeId()).thenReturn(VALID_ID);

        Mono<?> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNextMatches(response -> response instanceof ResponseEntity &&
                        ((ResponseEntity<?>) response).getStatusCode() == HttpStatus.OK &&
                        ((ResponseEntity<?>) response).getBody() instanceof ChallengeDto &&
                        ((ChallengeDto) ((ResponseEntity<?>) response).getBody()).getChallengeId().equals(VALID_ID))
                .verifyComplete();
    }

    @Test
    void getChallengeId_Empty() {

        when(challengeDto.getChallengeId()).thenReturn(null);

        Mono<?> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNextMatches(response -> response instanceof ResponseEntity &&
                        ((ResponseEntity<?>) response).getStatusCode() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void testIsValidUUID_Ok() {
        boolean result = challengeService.isValidUUID(VALID_ID.toString());

        assertTrue(result);
    }

    @Test
    void testIsValidUUID_NotValid() {
        boolean result = challengeService.isValidUUID(INVALID_ID);

        assertFalse(result);
    }

}
