package com.itachallenge.challenge.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.itachallenge.challenge.documents.Challenge;
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

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
public class ChallengeServiceImpTest {
    //variables
    private final static UUID VALID_ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
    private final static String INVALID_ID = "123456789";
    private final String CONTROLLER_BASE_URL = "/itachallenge/api/v1/challenge";
    final String URI_TEST = "/test";

    @Autowired
    private WebTestClient webTestClient;
    @InjectMocks
    private ChallengeServiceImp challengeService;
    @MockBean
    private ChallengeRepository challengeRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }

    @Test
    public void testGetChallengeIdValid() {
        Challenge challenge = new Challenge();
        when(challengeRepository.findById(VALID_ID)).thenReturn(Mono.just(challenge));

        Mono<Challenge> result = challengeService.getChallengeId(VALID_ID);

        assertNotNull(result.block());
        assertEquals(challenge, result.block());

        verifyRepository();
    }

    @Test
    public void testGetChallengeIdEmpty() {
        when(challengeRepository.findById(VALID_ID)).thenReturn(Mono.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            challengeService.getChallengeId(VALID_ID).block();
        });

        verifyRepository();
    }

    @Test
    public void testIsValidUUIDValid() {
        boolean result = challengeService.isValidUUID(VALID_ID.toString());

        assertTrue(result);
    }

    @Test
    public void testIsValidUUIDd_ValidUUID() {
        boolean result = challengeService.isValidUUID(INVALID_ID);

        assertFalse(result);
    }

    public void verifyRepository(){
        verify(challengeRepository, times(1)).findById(VALID_ID);
        verifyNoMoreInteractions(challengeRepository);
    }

}
