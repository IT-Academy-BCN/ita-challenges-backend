package com.itachallenge.user.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.itachallenge.user.document.SolutionAttemptDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.UnmodificableSolutionException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.itachallenge.user.service.UserSolutionServiceImpl.POINTS_PER_SOLVED_CHALLENGE;
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

    @Mock
    private UserServiceImpl userService;

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

        when(userService.addPointsToUser(userUuid.toString(), POINTS_PER_SOLVED_CHALLENGE))
                .thenReturn(Mono.just(true));

        when(challengeService.addChallengeToSolved(challengeUuid.toString()))
                .thenReturn(Mono.just(new com.itachallenge.user.dto.SolvedDto(true, 5)));

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
    @DisplayName("addSolution updates existing solution when status is not ENDED")
    void addSolutionUpdatesExistingSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("IN_PROGRESS")
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
                })
                .verifyComplete();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
        verifyNoInteractions(challengeService);
    }

    @Test
    @DisplayName("addSolution throws UnmodificableSolutionException if existing solution status is ENDED")
    void addSolutionThrowsExceptionIfEnded() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("ENDED")
                .solutionText(solutionText)
                .build();

        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(com.itachallenge.user.document.enums.ChallengeStatus.ENDED)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("Old solution").build())
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingSolution));

        StepVerifier.create(userSolutionService.addSolution(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof UnmodificableSolutionException &&
                                throwable.getMessage().contains("Existing solution is already ENDED"))
                .verify();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
        verifyNoInteractions(challengeService);
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
                .status(com.itachallenge.user.document.enums.ChallengeStatus.IN_PROGRESS)
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

    @Test
    @DisplayName("getAllSolutionsByUser returns ENDED solution")
    void getAllSolutionsByUser_returnsEndedSolution() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionAttemptDocument(SolutionAttemptDocument.builder().solutionText("Ended solution").build())
                .status(com.itachallenge.user.document.enums.ChallengeStatus.ENDED)
                .build();

        when(userSolutionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.just(doc));

        StepVerifier.create(userSolutionService.getAllSolutionsByUser(userUuid.toString()))
                .assertNext(dto -> {
                    assertEquals("Ended solution", dto.getSolutionText());
                    assertEquals(userUuid.toString(), dto.getUserId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("getAllSolutionsByUser returns empty when user has no solutions")
    void getAllSolutionsByUser_returnsEmptyWhenNoSolutions() {
        when(userSolutionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.empty());

        StepVerifier.create(userSolutionService.getAllSolutionsByUser(userUuid.toString()))
                .expectNextCount(0)
                .verifyComplete();
    }


    @Test
    @DisplayName("addSolution creates new IN_PROGRESS solution and returns response")
    void addSolutionNewInProgressSolution() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("IN_PROGRESS")  // ⬅️ acá cambiamos ENDED por IN_PROGRESS
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


    @Test
    @DisplayName("awardsPointsForSolvedChallenge returns document unchanged if status is not ENDED")
    void awardsPointsForSolvedChallenge_notEnded() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.IN_PROGRESS)  // not ENDED
                .build();

        Mono<UserSolutionDocument> result = userSolutionService.awardsPointsForSolvedChallenge(doc);

        StepVerifier.create(result)
                .assertNext(returnedDoc -> assertEquals(doc, returnedDoc))
                .verifyComplete();

        // userService should NOT be called
        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("awardsPointsForSolvedChallenge calls userService when status is ENDED")
    void awardsPointsForSolvedChallenge_ended() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.ENDED)
                .build();

        when(userService.addPointsToUser(eq(userUuid.toString()), anyInt()))
                .thenReturn(Mono.empty());

        Mono<UserSolutionDocument> result = userSolutionService.awardsPointsForSolvedChallenge(doc);

        StepVerifier.create(result)
                .assertNext(returnedDoc -> assertEquals(doc, returnedDoc))
                .verifyComplete();

        verify(userService).addPointsToUser(userUuid.toString(), POINTS_PER_SOLVED_CHALLENGE); // assuming POINTS_PER_SOLVED_CHALLENGE = 10
    }

    @Test
    @DisplayName("awardsPointsForSolvedChallenge logs INFO when addPointsToUser returns true")
    void awardsPointsForSolvedChallenge_logsInfoWhenTrue() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .status(ChallengeStatus.ENDED)
                .build();

        when(userService.addPointsToUser(anyString(), anyInt()))
                .thenReturn(Mono.just(true));

        Logger logger = (Logger) LoggerFactory.getLogger(UserSolutionServiceImpl.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        StepVerifier.create(userSolutionService.awardsPointsForSolvedChallenge(doc))
                .expectNext(doc)
                .verifyComplete();

        assertTrue(appender.list.stream()
                        .anyMatch(e -> e.getLevel() == Level.INFO &&
                                e.getFormattedMessage().contains("Awarded ")),
                "Expected INFO log for awarded points");
    }

    @Test
    @DisplayName("awardsPointsForSolvedChallenge logs WARN when addPointsToUser returns false")
    void awardsPointsForSolvedChallenge_logsWarnWhenFalse() {
        UserSolutionDocument doc = UserSolutionDocument.builder()
                .userId(userUuid)
                .status(ChallengeStatus.ENDED)
                .build();

        when(userService.addPointsToUser(anyString(), anyInt()))
                .thenReturn(Mono.just(false));

        Logger logger = (Logger) LoggerFactory.getLogger(UserSolutionServiceImpl.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        StepVerifier.create(userSolutionService.awardsPointsForSolvedChallenge(doc))
                .expectNext(doc)
                .verifyComplete();

        assertTrue(appender.list.stream()
                        .anyMatch(e -> e.getLevel() == Level.WARN &&
                                e.getFormattedMessage().contains("Failed to award")),
                "Expected WARN log for failed awarding");
    }

    @Test
    @DisplayName("addSolution awards points when status is ENDED")
    void addSolutionAwardsPointsWhenEnded() {
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

        when(userService.addPointsToUser(userUuid.toString(), POINTS_PER_SOLVED_CHALLENGE))
                .thenReturn(Mono.just(true));

        when(challengeService.addChallengeToSolved(challengeUuid.toString()))
                .thenReturn(Mono.just(new com.itachallenge.user.dto.SolvedDto(true, 3)));

        StepVerifier.create(userSolutionService.addSolution(request))
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertTrue(dto.getIsSolved());
                    assertEquals(3, dto.getTimesSolved());
                    assertEquals("ENDED", dto.getStatus());
                })
                .verifyComplete();

        verify(userService).addPointsToUser(userUuid.toString(), POINTS_PER_SOLVED_CHALLENGE);
        verify(challengeService).addChallengeToSolved(challengeUuid.toString());
    }

    @Test
    @DisplayName("addSolution does not award points when status is IN_PROGRESS")
    void addSolutionDoesNotAwardPointsWhenNotEnded() {
        UserSolutionRequestDto request = UserSolutionRequestDto.builder()
                .userId(userUuid.toString())
                .challengeId(challengeUuid.toString())
                .languageId(languageUuid.toString())
                .status("IN_PROGRESS")
                .solutionText(solutionText)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userSolutionService.addSolution(request))
                .assertNext(dto -> {
                    assertEquals(solutionText, dto.getSolutionText());
                    assertFalse(dto.getIsSolved());
                    assertEquals("IN_PROGRESS", dto.getStatus());
                })
                .verifyComplete();

        verifyNoInteractions(userService);
        verifyNoInteractions(challengeService);
    }

}
