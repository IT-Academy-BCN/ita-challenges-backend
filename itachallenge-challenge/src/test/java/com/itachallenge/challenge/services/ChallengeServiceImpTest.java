package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dtos.ChallengeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
class ChallengeServiceImpTest {
    //variables
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
    void testGetChallengeId_ValidId() {
        ChallengeDto expectedDto = new ChallengeDto();
        expectedDto.setId_challenge(VALID_ID);

        when(challengeDto.getId_challenge()).thenReturn(VALID_ID);

        Mono<ChallengeDto> result = challengeService.getChallengeId(VALID_ID);

        assertNotNull(result);
        assertEquals(expectedDto.getId_challenge(), result.block().getId_challenge());

        verify(challengeDto, times(1)).getId_challenge();
    }

    @Test
    void testGetChallengeId_NotExist() {
        Mono<ChallengeDto> resultMono = challengeService.getChallengeId(VALID_ID);

        resultMono.subscribe(result -> { //result is false
            assert false;
        }
        , error -> { //check expected error when result is false
            assert error instanceof IllegalArgumentException;
            assert error.getMessage().equals("ID challenge: " + VALID_ID + " does not exist in the database.");
        });
    }

    @Test
    void testGetChallengeId_IllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            challengeService.getChallengeId(UUID.fromString(INVALID_ID)).block();
        });
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
