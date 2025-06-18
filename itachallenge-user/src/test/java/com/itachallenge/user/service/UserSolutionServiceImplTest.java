package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dto.SolvedDto;
import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImplTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;

    @Mock
    private IChallengeService challengeService;

    @InjectMocks
    private UserSolutionServiceImpl userSolutionService;

    private UUID userUuid;
    private UUID challengeUuid;
    private UUID languageUuid;
    private String solutionText;

    @BeforeEach
    void setUp() {
        userUuid = UUID.randomUUID();
        challengeUuid = UUID.randomUUID();
        languageUuid = UUID.randomUUID();
        solutionText = "Test solution";
    }

    @Test
    @DisplayName("addSolution creates new ENDED solution and returns response")
    void addSolutionNewEndedSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("ENDED")
                .solutionText(solutionText)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(challengeService.addChallengeToSolved(challengeUuid.toString()))
                .thenReturn(Mono.just(new SolvedDto(true, 5)));

        Mono<SubmitSolutionResponseDto> result = userSolutionService.addSolution(request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertTrue(dto.getIsSolved());
                    assertEquals(5, dto.getTimesSolved());
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verify(challengeService).addChallengeToSolved(challengeUuid.toString());
    }


    @Test
    @DisplayName("addSolution throws exception for invalid status")
    void addSolutionInvalidStatus() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("INVALID")
                .solutionText(solutionText)
                .build();

        StepVerifier.create(userSolutionService.addSolution(request))
                .expectErrorMatches(err -> err instanceof IllegalArgumentException &&
                        err.getMessage().equals("Status null or not allowed"))
                .verify();
    }

    @Test
    @DisplayName("getAllSolutionsByUser with invalid UUID throws BadRequestException")
    void getAllSolutionsByUser_invalidUuid() {
        StepVerifier.create(userSolutionService.getAllSolutionsByUser("bad-uuid"))
                .expectErrorMatches(ex -> ex instanceof BadRequestException &&
                        ex.getMessage().contains("must be a valid UUID"))
                .verify();
    }

    @Test
    @DisplayName("getAllSolutionsByUser with valid UUID returns solutions")
    void getAllSolutionsByUser_returnsSolutions() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("My solution").build())
                .build();

        when(userSolutionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.just(doc));

        StepVerifier.create(userSolutionService.getAllSolutionsByUser(userUuid.toString()))
                .assertNext(dto -> {
                    assertEquals("My solution", dto.getSolutionText());
                    assertEquals(userUuid.toString(), dto.getUserId());
                })
                .verifyComplete();
    }
}
