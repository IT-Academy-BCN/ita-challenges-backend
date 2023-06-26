package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.helper.ModelMapperConverters;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;
    @Mock
    private ModelMapperConverters modelMapper;
    @InjectMocks
    private ChallengeService challengeService;

    private Challenge challenge1, challenge2;
    private ChallengeDto challengeDto1, challengeDto2;

    @BeforeEach
    public void setup() {
        String uuidString = "4020e1e1-e6b2-4c20-817b-70193b518b3f";
        UUID uuid1 = UUID.fromString(uuidString);
        String uuidString2 = "5020e1e1-e6b2-4c20-817b-70193b518b3f";
        UUID uuid2 = UUID.fromString(uuidString2);

        challenge1 = new Challenge(uuid1, "Level 1", "Challenge 1",
                new HashSet<>(Arrays.asList("Java", "PHP")), LocalDateTime.now(), null, null, null, null);
        challenge2 = new Challenge(uuid2, "Level 2", "Challenge 2",
                new HashSet<>(Arrays.asList("Python")), LocalDateTime.now(), null, null, null, null);

        challengeDto1 = ChallengeDto.builder().challengeId(uuid1).level("Level 1").title("Challenge 1").build();
        challengeDto2 = ChallengeDto.builder().challengeId(uuid2).level("Level 2").title("Challenge 2").build();
    }

    @Test
    public void testGetChallenges() {

        Flux<Challenge> mockChallenges = Flux.just(challenge1, challenge2);
        when(challengeRepository.findAll()).thenReturn(mockChallenges);

        Flux<ChallengeDto> expectedDtoList = Flux.just(challengeDto1, challengeDto2);
        when(modelMapper.convertChallengesToDTO(mockChallenges)).thenReturn(expectedDtoList);

        Flux<ChallengeDto> result = challengeService.getChallenges();
        verify(challengeRepository).findAll();
        verify(modelMapper).convertChallengesToDTO(mockChallenges);

        StepVerifier.create(result)
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }
}
