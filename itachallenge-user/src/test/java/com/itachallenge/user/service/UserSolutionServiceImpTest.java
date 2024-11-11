package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.dtos.zmq.ScoreRequestDto;
import com.itachallenge.user.dtos.zmq.ScoreResponseDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.SolutionNotFoundException;
import com.itachallenge.user.exception.UnmodifiableSolutionException;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.mqclient.ZMQClient;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.InjectMocks;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImpTest {

    @Mock
    IUserSolutionRepository userSolutionRepository;
    @Mock
    private ConverterDocumentToDto converter;
    @Mock
    private ZMQClient zmqClient;
    @InjectMocks
    UserSolutionServiceImp userSolutionService;

    private String solutionText;
    private UUID userUuid;
    private UUID challengeUuid;
    private UUID languageUuid;
    private int mockScore;
    private String mockErrors;
    private UserSolutionDto userSolutionDto;
    private UserSolutionDocument userSolutionDocument;

    @BeforeEach
    void setUp() {
        String idUser = "550e8400-e29b-41d4-a716-446655440001";
        String idChallenge = "550e8400-e29b-41d4-a716-446655440002";
        String idLanguage = "550e8400-e29b-41d4-a716-446655440003";
        solutionText = "This is a test started solution";
        userUuid = UUID.fromString(idUser);
        challengeUuid = UUID.fromString(idChallenge);
        languageUuid = UUID.fromString(idLanguage);
        mockScore = 13;
        mockErrors = "xxx";
        userSolutionDto = UserSolutionDto.builder()
                .userId(idUser)
                .challengeId(idChallenge)
                .languageId(idLanguage)
                .status("ENDED")
                .solutionText(solutionText).build();
        userSolutionDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.ENDED)
                .score(mockScore)
                .errors(mockErrors)
                .solutionDocument(List.of(SolutionDocument.builder().solutionText(solutionText).build()))
                .build();
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(any(UUID.class), any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

    }

    @Test
    void markAsBookmarked() {
        UUID challengeId = UUID.fromString("b860f3eb-ef9f-43bf-8c3c-9a5318d26a90");
        UUID languageId = UUID.fromString("26cbe8eb-be68-4eb4-96a6-796168e80ec9");
        UUID userId = UUID.fromString("df99bae8-4f7f-4054-a957-37a12aa16364");
        boolean bookmarked = true;
        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        userSolutionDocument.setUserId(userId);
        userSolutionDocument.setLanguageId(languageId);
        userSolutionDocument.setChallengeId(challengeId);
        userSolutionDocument.setBookmarked(true);
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId))
                .thenReturn(Mono.just(userSolutionDocument));

        assertNotNull(userSolutionDocument);
        assert (userSolutionDocument.isBookmarked());
        assert (userSolutionDocument.getUserId().equals(userId));
        assert (userSolutionDocument.getLanguageId().equals(languageId));
        assert (userSolutionDocument.getChallengeId().equals(challengeId));

    }

    @DisplayName("UserSolutionServiceImpTest - getChallengeById returns a SolutionUserDto when a valid document is found")
    @Test
    void getChallengeByIdTest() {

        ConverterDocumentToDto converter = new ConverterDocumentToDto();

        userSolutionDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.ENDED)
                .solutionDocument(List.of(SolutionDocument.builder().solutionText(solutionText).build()))
                .score(mockScore)
                .errors(mockErrors)
                .build();
        when(userSolutionRepository.findByUserId(userUuid)).thenReturn(Flux.just(userSolutionDocument));
        UserSolutionServiceImp userSolutionServiceImp = new UserSolutionServiceImp(userSolutionRepository, converter, zmqClient);

        Mono<SolutionUserDto<UserScoreDto>> challengeById = userSolutionServiceImp.getChallengeById(userUuid.toString(), challengeUuid.toString(), languageUuid.toString());

        assertNotNull(challengeById);
        StepVerifier.create(challengeById)
                .expectNextMatches(solutionUserDto -> solutionUserDto.getCount() == 1
                        && solutionUserDto.getLimit() == 1
                        && solutionUserDto.getOffset() == 0
                        && solutionUserDto.getResults().length == 1)
                .verifyComplete();
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns UnmodifiableSolutionException when status is ENDED")
    @Test
    void addSolutionWithEndedStatus() {
        // Configura el estado de la solución como ENDED
        userSolutionDto.setStatus("ENDED");

        // Simula la existencia de una solución con el estado ENDED
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.ENDED);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

        // Verifica que se lanza la excepción UnmodifiableSolutionException con el mensaje adecuado
        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectErrorMatches(
                        throwable -> throwable instanceof UnmodifiableSolutionException
                                && throwable.getMessage().equals("Cannot modify solution with status ENDED or SCORE_PENDING"))
                .verify();

        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns UnmodifiableSolutionException when existing solution status is SCORE_PENDING")
    @Test
    void addSolutionWithScorePendingStatus() {
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.SCORE_PENDING);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectErrorMatches(
                        throwable -> throwable instanceof UnmodifiableSolutionException
                                && throwable.getMessage().equals("Cannot modify solution with status ENDED or SCORE_PENDING")).verify();
        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution creates a new document when status is SENT")
    @Test
    void addSolutionNewSolutionWithSentStatus() {
        userSolutionDto.setStatus("SENT");

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        ScoreResponseDto scoreResponseDto = new ScoreResponseDto();
        scoreResponseDto.setScore(mockScore);
        scoreResponseDto.setErrors(mockErrors);

        CompletableFuture<Object> completableFuture = CompletableFuture.completedFuture(scoreResponseDto);

        when(zmqClient.sendMessage(any(), any()))
                .thenReturn(completableFuture);
        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getUserId().equals(userUuid.toString())
                                && userSolutionScoreDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionScoreDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionScoreDto.getSolutionText().equals(solutionText)
                                && userSolutionScoreDto.getScore() == mockScore
                                && userSolutionScoreDto.getErrors().equals(mockErrors))
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("saveValidSolution modifies existing solution when status is STARTED")
    @Test
    void saveValidSolutionModifiesExistingSolutionWhenStatusIsStarted() {
        UUID localUserUuid = UUID.randomUUID();
        UUID localChallengeUuid = UUID.randomUUID();
        UUID localLanguageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());
        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .status(ChallengeStatus.STARTED)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(localUserUuid, localChallengeUuid, localLanguageUuid))
                .thenReturn(Mono.just(existingSolution));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(existingSolution));

        StepVerifier.create(userSolutionService.saveValidSolution(localUserUuid, localChallengeUuid, localLanguageUuid, ChallengeStatus.STARTED, solutionDocuments))
                .expectNextMatches(savedDocument -> savedDocument.getSolutionDocument().equals(solutionDocuments))
                .verifyComplete();

        verify(userSolutionRepository).save(existingSolution);
    }

    @Test
    void addSolutionShouldThrowIllegalArgumentExceptionWhenChallengeStatusIsNull() {
        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setChallengeId(UUID.randomUUID().toString());
        userSolutionDto.setLanguageId(UUID.randomUUID().toString());
        userSolutionDto.setUserId(UUID.randomUUID().toString());
        userSolutionDto.setStatus(null); // Setting status to null

        Mono<UserSolutionScoreDto> result = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Status not allowed"))
                .verify();
    }

    @DisplayName("addSolution returns IllegalArgumentException when challenge status is null")
    @Test
    void addSolutionReturnsIllegalArgumentExceptionWhenChallengeStatusIsNull() {
        userSolutionDto.setStatus(null);

        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals("Status not allowed"))
                .verify();
    }


    @DisplayName("saveValidSolution returns error when existing solution status is ENDED")
    @Test
    void saveValidSolutionReturnsErrorWhenExistingSolutionStatusIsEnded() {
        UUID localUserUuid = UUID.randomUUID();
        UUID localChallengeUuid = UUID.randomUUID();
        UUID localLanguageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());
        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .status(ChallengeStatus.ENDED)
                .build();
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(localUserUuid, localChallengeUuid, localLanguageUuid))
                .thenReturn(Mono.just(existingSolution));
        StepVerifier.create(userSolutionService.saveValidSolution(localUserUuid, localChallengeUuid, localLanguageUuid, ChallengeStatus.STARTED, solutionDocuments))
                .expectError(UnmodifiableSolutionException.class)
                .verify();
        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }

    @DisplayName("saveValidSolution creates new solution when status is SENT")
    @Test
    void saveValidSolutionCreatesNewSolutionWhenStatusIsSent() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());
        ScoreResponseDto scoreResponseDto = new ScoreResponseDto();
        scoreResponseDto.setScore(100);
        scoreResponseDto.setErrors("No errors");

        UserSolutionDocument userSolutionDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionDocument(solutionDocuments)
                .status(ChallengeStatus.SENT)
                .score(100)  // Set the score
                .errors("No errors")  // Set the errors
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(scoreResponseDto));

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.SENT, solutionDocuments))
                .expectNextMatches(savedDocument -> savedDocument.getScore() == 100 && "No errors".equals(savedDocument.getErrors()))
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }


    @DisplayName("saveValidSolution returns empty Mono when no valid status is provided")
    @Test
    void saveValidSolutionReturnsEmptyMonoWhenNoValidStatusIsProvided() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.EMPTY, solutionDocuments))
                .verifyComplete();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }

    @DisplayName("saveValidSolution returns empty when status is not SENT or STARTED")
    @Test
    void saveValidSolutionReturnsEmptyWhenStatusIsNotSentOrStarted() {
        UUID localUserUuid = UUID.randomUUID();
        UUID localChallengeUuid = UUID.randomUUID();
        UUID localLanguageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(localUserUuid, localChallengeUuid, localLanguageUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(userSolutionService.saveValidSolution(localUserUuid, localChallengeUuid, localLanguageUuid, ChallengeStatus.ENDED, solutionDocuments))
                .verifyComplete();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }

    @DisplayName("saveValidSolution returns empty when no valid status is provided")
    @Test
    void saveValidSolutionReturnsEmptyWhenNoValidStatusIsProvided() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.EMPTY, solutionDocuments))
                .verifyComplete();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }


    @DisplayName("getDataFromMicroScore returns valid ScoreResponseDto")
    @Test
    void getDataFromMicroScoreReturnsValidResponse() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        String localsolutionText = "Sample solution text";
        ScoreResponseDto expectedResponse = new ScoreResponseDto();
        expectedResponse.setScore(100);
        expectedResponse.setErrors("No errors");

        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedResponse));

        CompletableFuture<ScoreResponseDto> resultFuture = userSolutionService.getDataFromMicroScore(challengeId, languageId, localsolutionText);

        assertNotNull(resultFuture);
        assertEquals(expectedResponse, resultFuture.join());
    }

    @DisplayName("getDataFromMicroScore handles exception and returns default ScoreResponseDto")
    @Test
    void getDataFromMicroScoreHandlesException() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        String localsolutionText = "Sample solution text";

        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("ZMQ error")));

        CompletableFuture<ScoreResponseDto> resultFuture = userSolutionService.getDataFromMicroScore(challengeId, languageId, localsolutionText);

        assertNotNull(resultFuture);
        ScoreResponseDto result = resultFuture.join();
        assertEquals(0, result.getScore());
        assertNull(result.getErrors());
    }

    @DisplayName("UserSolutionServiceImpTest - showAllUserSolutions returns all solutions for the user")
    @Test
    void showAllUserSolutions() {
        UserSolutionDto userSolutionDto = UserSolutionDto.builder()
                .userId(userUuid.toString())
                .challengeId(userSolutionDocument.getChallengeId().toString())
                .languageId(userSolutionDocument.getLanguageId().toString())
                .status(userSolutionDocument.getStatus().toString())
                .solutionText("Sample Solution")
                .build();

        when(userSolutionRepository.findByUserId(userUuid)).thenReturn(Flux.just(userSolutionDocument));
        when(converter.fromUserSolutionDocumentToUserSolutionDto(userSolutionDocument)).thenReturn(Flux.just(userSolutionDto));

        Flux<UserSolutionDto> resultFlux = userSolutionService.showAllUserSolutions(userUuid);

        StepVerifier.create(resultFlux)
                .expectNextMatches(dto ->
                        dto.getUserId().equals(userUuid.toString()) &&
                                dto.getSolutionText().equals("Sample Solution"))
                .verifyComplete();
    }


    @DisplayName("UserSolutionServiceImpTest - showAllUserSolutions returns empty flux when no solutions are found")
    @Test
    void showAllUserSolutions_NoSolutions() {
        when(userSolutionRepository.findByUserId(userUuid)).thenReturn(Flux.empty());

        Flux<UserSolutionDto> resultFlux = userSolutionService.showAllUserSolutions(userUuid);

        StepVerifier.create(resultFlux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void getChallengeStatistics() {
        List<UUID> challengeIds;
        List<ChallengeStatisticsDto> challengeList;
        Mono<List<ChallengeStatisticsDto>> result;

        challengeIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        result = userSolutionService.getChallengeStatistics(challengeIds);
        challengeList = result.block();

        assertNotNull(challengeList);
        assertEquals(challengeIds.size(), challengeList.size());
    }

    @DisplayName("markAsBookmarked updates existing document")
    @Test
    void markAsBookmarkedUpdatesExistingDocument() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        boolean bookmarked = true;

        UserSolutionDocument existingDocument = UserSolutionDocument.builder()
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .bookmarked(false)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId))
                .thenReturn(Mono.just(existingDocument));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(existingDocument));

        StepVerifier.create(userSolutionService.markAsBookmarked(challengeId.toString(), languageId.toString(), userId.toString(), bookmarked))
                .expectNextMatches(document -> document.isBookmarked() == bookmarked)
                .verifyComplete();

        verify(userSolutionRepository).save(existingDocument);
    }

    @DisplayName("markAsBookmarked creates new document if not found")
    @Test
    void markAsBookmarkedCreatesNewDocumentIfNotFound() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        boolean bookmarked = true;

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId))
                .thenReturn(Mono.empty());
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userSolutionService.markAsBookmarked(challengeId.toString(), languageId.toString(), userId.toString(), bookmarked))
                .expectNextMatches(document -> document.isBookmarked() == bookmarked)
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("createAndSaveNewBookmark creates and saves new document")
    @Test
    void createAndSaveNewBookmarkCreatesAndSavesNewDocument() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        boolean bookmarked = true;

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userSolutionService.createAndSaveNewBookmark(challengeId, languageId, userId, bookmarked))
                .expectNextMatches(document -> document.isBookmarked() == bookmarked)
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("Should return number of BookmarkedTrue by idChallenge")
    @Test
    void testGetBookmarkCountByIdChallenge() {
        UUID idChallenge = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        boolean isBookmarked = true;
        long expectedValue = 2L;

        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        UserSolutionDocument userSolutionDocument2 = new UserSolutionDocument();
        userSolutionDocument.setChallengeId(idChallenge);
        userSolutionDocument2.setChallengeId(idChallenge);
        userSolutionDocument.setBookmarked(true);
        userSolutionDocument2.setBookmarked(true);

        when(userSolutionRepository.countByChallengeIdAndBookmarked(idChallenge, isBookmarked))
                .thenReturn(Mono.just(2L));

        Mono<Long> resultMono = userSolutionService.getBookmarkCountByIdChallenge(idChallenge);

        StepVerifier.create(resultMono)
                .expectNext(expectedValue)
                .verifyComplete();

    }

    @DisplayName("determineChallengeStatus returns STARTED when status is null")
    @Test
    void determineChallengeStatusReturnsStartedWhenStatusIsNull() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus(null);
        assertEquals(ChallengeStatus.STARTED, result);
    }

    @DisplayName("determineChallengeStatus returns STARTED when status is empty")
    @Test
    void determineChallengeStatusReturnsStartedWhenStatusIsEmpty() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("");
        assertEquals(ChallengeStatus.STARTED, result);
    }

    @DisplayName("determineChallengeStatus returns EMPTY when status is EMPTY")
    @Test
    void determineChallengeStatusReturnsEmptyWhenStatusIsEmpty() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("EMPTY");
        assertEquals(ChallengeStatus.EMPTY, result);
    }

    @DisplayName("determineChallengeStatus returns SENT when status is SENT")
    @Test
    void determineChallengeStatusReturnsSentWhenStatusIsSent() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("SENT");
        assertEquals(ChallengeStatus.SENT, result);
    }

    @DisplayName("determineChallengeStatus returns SCORE_PENDING when status is SCORE_PENDING")
    @Test
    void determineChallengeStatusReturnsScorePendingWhenStatusIsScorePending() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("SCORE_PENDING");
        assertEquals(ChallengeStatus.SCORE_PENDING, result);
    }

    @DisplayName("determineChallengeStatus returns ENDED when status is ENDED")
    @Test
    void determineChallengeStatusReturnsEndedWhenStatusIsEnded() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("ENDED");
        assertEquals(ChallengeStatus.ENDED, result);
    }

    @DisplayName("determineChallengeStatus returns null for unknown status")
    @Test
    void determineChallengeStatusReturnsNullForUnknownStatus() {
        ChallengeStatus result = userSolutionService.determineChallengeStatus("UNKNOWN");
        assertNull(result);
    }


    @Test
    void getChallengeUsersPercentageTest() {

        List<SolutionDocument> solutionField = Arrays.asList(new SolutionDocument(UUID.randomUUID(), "solution1Text"));
        UUID challengeId = UUID.fromString("7fc6a737-dc36-4e1b-87f3-120d81c548aa");
        float expectedValue = 100f;

        List<UserSolutionDocument> userSolutions = Arrays.asList(
                new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), challengeId, UUID.randomUUID(), false, ChallengeStatus.STARTED, 45, mockErrors, solutionField),
                new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), challengeId, UUID.randomUUID(), false, ChallengeStatus.ENDED, 75, mockErrors, solutionField)
        );

        when(userSolutionRepository.findByChallengeIdAndStatus(challengeId, ChallengeStatus.STARTED)).thenReturn(Flux.fromIterable(
                userSolutions.stream().filter(s -> s.getStatus() == ChallengeStatus.STARTED).toList()));
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeId, ChallengeStatus.ENDED)).thenReturn(Flux.fromIterable(
                userSolutions.stream().filter(s -> s.getStatus() == ChallengeStatus.ENDED).toList()));
        when(userSolutionRepository.findByChallengeId(challengeId)).thenReturn(Flux.fromIterable(userSolutions));

        Mono<Float> result = userSolutionService.getChallengeUsersPercentage(challengeId);

        StepVerifier.create(result)
                .expectNext(expectedValue)
                .verifyComplete();
    }

    @Test // Michel: refactoring all tests in 1 single class
    void getUserScoreByUserId() {

        UUID userId = UUID.randomUUID();
        UUID idLanguage = UUID.randomUUID();
        UUID idChallenge = UUID.randomUUID();

        SolutionDocument solutionDocument1 = new SolutionDocument(UUID.randomUUID(), "solutionText1");
        SolutionDocument solutionDocument2 = new SolutionDocument(UUID.randomUUID(), "solutionText2");
        SolutionDocument solutionDocument3 = new SolutionDocument(UUID.randomUUID(), "solutionText3");
        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2, solutionDocument3);

        UserSolutionDocument userSolutionDoc = new UserSolutionDocument(UUID.randomUUID(), userId, idChallenge, idLanguage, true, ChallengeStatus.STARTED, 1, "x", solutionDocumentList);
        UserScoreDto userScoreDto = new UserScoreDto();
        SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0, 1, 0, new UserScoreDto[]{userScoreDto});

        when(userSolutionRepository.findByUserId(userId)).thenReturn(Flux.just(userSolutionDoc));
        when(converter.fromUserScoreDocumentToUserScoreDto(any())).thenReturn(Flux.just(userScoreDto));

        Mono<SolutionUserDto<UserScoreDto>> result = userSolutionService.getChallengeById(userId.toString(), idChallenge.toString(), idLanguage.toString());

        StepVerifier.create(result)
                .expectNextMatches(dto -> Arrays.equals(dto.getResults(), expectedSolutionUserDto.getResults()))
                .expectComplete()
                .verify();
    }
}