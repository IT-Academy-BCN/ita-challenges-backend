package com.itachallenge.challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.helpers.ChallengeMapper;
import com.itachallenge.challenge.repositories.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
public class ChallengeServiceImpTest {
    //variables
    private final static UUID VALID_ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
    private final static String INVALID_ID = "123456789";

    @Autowired
    private WebTestClient webTestClient;
    @InjectMocks
    private ChallengeServiceImp challengeService;
    @MockBean
    private ChallengeRepository challengeRepository;
    @MockBean
    private ChallengeMapper challengeMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }

    @Test
    public void testGetChallengeId_Valid() {
        Challenge createChallenge = new Challenge();
        ChallengeDto createChallengeDto = new ChallengeDto();

        when(challengeRepository.findById(VALID_ID)).thenReturn(Mono.just(createChallenge));
        when(challengeMapper.mapToChallengeDto(createChallenge)).thenReturn(createChallengeDto);

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);
        ChallengeDto challengeDto = result.block();

        assertNotNull(challengeDto);
        assertEquals(challengeDto, challengeDto);

        verify(challengeRepository, times(1)).findById(VALID_ID);
        verify(challengeMapper, times(1)).mapToChallengeDto(createChallenge);
        verifyNoMoreInteractions(challengeRepository);
    }

    @Test
    public void testGetChallengeId_Empty() {
        when(challengeRepository.findById(VALID_ID)).thenReturn(Mono.empty());

        StepVerifier.create(challengeService.getChallengeId(VALID_ID))
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(challengeRepository, times(1)).findById(VALID_ID);
        verifyNoMoreInteractions(challengeRepository);
    }

    @Test
    public void testIsValidUUID_Ok() {
        boolean result = challengeService.isValidUUID(VALID_ID.toString());

        assertTrue(result);
    }

    @Test
    public void testIsValidUUID_NotValid() {
        boolean result = challengeService.isValidUUID(INVALID_ID);

        assertFalse(result);
    }

}
