package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.document.enums.SolutionAction;
import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.UnmodificableSolutionException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImplTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;

    @Mock
    private IChallengeService challengeService;

    @MockBean
    private ExternalGithubService externalGithubService;

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
    @DisplayName("addSolution SAVE action updates existing IN_PROGRESS solution successfully")
    void addSolutionUpdatesExistingSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(SolutionAction.SAVE)
                .solutionText(solutionText)
                .build();

        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(com.itachallenge.user.document.enums.ChallengeStatus.IN_PROGRESS)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("Old solution").build())
                .build();

        UserSolutionDocument savedSolution = UserSolutionDocument.builder()
                .uuid(existingSolution.getUuid())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(com.itachallenge.user.document.enums.ChallengeStatus.IN_PROGRESS)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText(solutionText).build())
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingSolution));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(savedSolution));

        Mono<SubmitSolutionResponseDto> result = userSolutionService.addSolution(request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertFalse(dto.getIsSolved());
                    assertNull(dto.getTimesSolved());
                    assertEquals("IN_PROGRESS", dto.getStatus());
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verifyNoInteractions(challengeService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GIVE_UP", "SUBMIT"})
    @DisplayName("addSolution throws UnmodificableSolutionException if existing solution status is already submitted")
    void addSolutionThrowsExceptionIfSubmitted(SolutionAction action) {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(action)
                .solutionText(solutionText)
                .build();

        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.SUBMITTED_COMPLETE)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("Old solution").build())
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingSolution));

        StepVerifier.create(userSolutionService.addSolution(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof UnmodificableSolutionException &&
                                throwable.getMessage().contains("Existing solution is already submitted and cannot be modified."))
                .verify();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
        verifyNoInteractions(challengeService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"GIVE_UP", "SUBMIT"})
    @DisplayName("addSolution throws UnmodificableSolutionException if existing solution status is already submitted")
    void addSolutionThrowsExceptionIfGivenUp(SolutionAction action) {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(action)
                .solutionText(solutionText)
                .build();

        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.SUBMITTED_INCOMPLETE)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("Old solution").build())
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingSolution));

        StepVerifier.create(userSolutionService.addSolution(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof UnmodificableSolutionException &&
                                throwable.getMessage().contains("Existing solution is already submitted and cannot be modified."))
                .verify();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
        verifyNoInteractions(challengeService);
    }

    @Test
    @DisplayName("addSolution creates new SUBMITTED_COMPLETE solution and returns response")
    void addSolutionNewSubmittedCompleteSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(SolutionAction.SUBMIT)
                .solutionText(solutionText)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(challengeService.addChallengeToSolved(challengeUuid.toString()))
                .thenReturn(Mono.just(new com.itachallenge.user.dto.SolvedDto(true, 5)));

        Mono<SubmitSolutionResponseDto> result = userSolutionService.addSolution(request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertTrue(dto.getIsSolved());
                    assertEquals(5, dto.getTimesSolved());
                    assertEquals("SUBMITTED_COMPLETE", dto.getStatus());
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verify(challengeService).addChallengeToSolved(challengeUuid.toString());
    }

    @Test
    @DisplayName("addSolution GIVE_UP action creates new SUBMITTED_INCOMPLETE solution and returns response")
    void addSolutionNewSubmittedIncompleteSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(SolutionAction.GIVE_UP)
                .solutionText(solutionText)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<SubmitSolutionResponseDto> result = userSolutionService.addSolution(request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertFalse(dto.getIsSolved());
                    assertEquals("SUBMITTED_INCOMPLETE", dto.getStatus());
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verifyNoInteractions(challengeService);
    }

    @Test
    @DisplayName("addSolution SAVE action creates new IN_PROGRESS solution and returns response")
    void addSolutionNewInProgressSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .action(SolutionAction.SAVE)
                .solutionText(solutionText)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<SubmitSolutionResponseDto> result = userSolutionService.addSolution(request);

        StepVerifier.create(result)
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertFalse(dto.getIsSolved());
                    assertEquals("IN_PROGRESS", dto.getStatus());
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verifyNoInteractions(challengeService);
    }
}
