package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;

import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
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
    private ChallengeRepository challengeRepository;
    @MockBean
    private Converter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChallengeId() {
        ChallengeDocument challenge = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findByUuid(VALID_ID)).thenReturn(Mono.just(challenge));
        when(converter.fromChallengeToChallengeDto(any(Flux.class))).thenReturn(Flux.just(challengeDto));

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);

        //Comprueba que devuelva un elemento únicamente (Mono)
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetChallengeId_ConvertDto() {
        ChallengeDocument challenge = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findByUuid(VALID_ID)).thenReturn(Mono.just(challenge));
        when(converter.fromChallengeToChallengeDto(any(Flux.class))).thenReturn(Flux.just(challengeDto));

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);

        StepVerifier.create(result)
                .expectNext(challengeDto)
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
