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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ChallengeServiceImp.class)
class ChallengeServiceImpTest {
    //VARIABLES
    private final static UUID VALID_ID = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
    private final static String INVALID_ID = "123456789";

    @MockBean
    private ChallengeRepository challengeRepository;
    @MockBean
    private Converter converter;
    @InjectMocks
    private ChallengeServiceImp challengeService;
    @MockBean
    private ChallengeDto challengeDto;
    private ChallengeDocument challenge1, challenge2;
    private ChallengeDto challengeDto1, challengeDto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        String uuidString = "4020e1e1-e6b2-4c20-817b-70193b518b3f";
        UUID uuid1 = UUID.fromString(uuidString);
        String uuidString2 = "5020e1e1-e6b2-4c20-817b-70193b518b3f";
        UUID uuid2 = UUID.fromString(uuidString2);

        challenge1 = new ChallengeDocument(uuid1, "Level 1", "Challenge 1",
                null, LocalDateTime.now(), null, null, null, null);
        challenge2 = new ChallengeDocument(uuid2, "Level 2", "Challenge 2",
                null, LocalDateTime.now(), null, null, null, null);

        challengeDto1 = ChallengeDto.builder().challengeId(uuid1).level("Level 1").title("Challenge 1").build();
        challengeDto2 = ChallengeDto.builder().challengeId(uuid2).level("Level 2").title("Challenge 2").build();
    }

    @Test
    void testGetChallengeId() {
        ChallengeDocument challenge = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findByIdChallenge(VALID_ID)).thenReturn(Mono.just(challenge));
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

        when(challengeRepository.findByIdChallenge(VALID_ID)).thenReturn(Mono.just(challenge));
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

    @Test
    void testGetChallenges() {

        Flux<ChallengeDocument> mockChallenges = Flux.just(challenge1, challenge2);
        when(challengeRepository.findAll()).thenReturn(mockChallenges);

        Flux<ChallengeDto> expectedDtoList = Flux.just(challengeDto1, challengeDto2);
        when(converter.fromChallengeToChallengeDto(mockChallenges)).thenReturn(expectedDtoList);

        Flux<ChallengeDto> result = challengeService.getChallenges();
        verify(challengeRepository).findAll();
        verify(converter).fromChallengeToChallengeDto(mockChallenges);

        StepVerifier.create(result)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

}
