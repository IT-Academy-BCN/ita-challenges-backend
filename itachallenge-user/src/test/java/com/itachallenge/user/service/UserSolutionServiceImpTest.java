package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.dtos.zmq.ScoreRequestDto;
import com.itachallenge.user.dtos.zmq.ScoreResponseDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.ChallengeNotFoundException;
import com.itachallenge.user.exception.UnmodifiableSolutionException;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.mqclient.ZMQClient;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
        UserSolutionDocument localUserSolutionDocument = new UserSolutionDocument();
        localUserSolutionDocument.setUserId(userId);
        localUserSolutionDocument.setLanguageId(languageId);
        localUserSolutionDocument.setChallengeId(challengeId);
        localUserSolutionDocument.setBookmarked(true);
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId))
                .thenReturn(Mono.just(localUserSolutionDocument));

        assertNotNull(localUserSolutionDocument);
        assertTrue(localUserSolutionDocument.isBookmarked());
        assertEquals(userId, localUserSolutionDocument.getUserId());
        assertEquals(languageId, localUserSolutionDocument.getLanguageId());
        assertEquals(challengeId, localUserSolutionDocument.getChallengeId());
    }

    @DisplayName("UserSolutionServiceImpTest - getChallengeById returns a SolutionUserDto when a valid document is found")
    @Test
    void getChallengeByIdTest() {

        ConverterDocumentToDto localConverter = new ConverterDocumentToDto();

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
        UserSolutionServiceImp userSolutionServiceImp = new UserSolutionServiceImp(userSolutionRepository, localConverter, zmqClient);

        Mono<SolutionUserDto<UserScoreDto>> challengeById = userSolutionServiceImp.getChallengeById(userUuid.toString(), challengeUuid.toString(), languageUuid.toString());

        assertNotNull(challengeById);
        StepVerifier.create(challengeById)
                .expectNextMatches(solutionUserDto -> solutionUserDto.getCount() == 1
                        && solutionUserDto.getLimit() == 1
                        && solutionUserDto.getOffset() == 0
                        && solutionUserDto.getResults().length == 1)
                .verifyComplete();
    }
    @ParameterizedTest
    @ValueSource(strings = { "", "null" })
    void addSolutionReturnsIllegalArgumentExceptionWhenStatusIsNullOrEmpty(String status) {
        if ("null".equals(status)) {
            status = null;
        }
        userSolutionDto.setStatus(status);
        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
    @DisplayName("addSolution returns UserSolutionScoreDto when solution is successfully added")
    @Test
    void addSolutionReturnsUserSolutionScoreDtoWhenSolutionIsSuccessfullyAdded() {
        userSolutionDto.setStatus("STARTED");

        UserSolutionDocument savedDocument = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(UUID.fromString(userSolutionDto.getUserId()))
                .challengeId(UUID.fromString(userSolutionDto.getChallengeId()))
                .languageId(UUID.fromString(userSolutionDto.getLanguageId()))
                .solutionDocument(List.of(SolutionDocument.builder().solutionText(userSolutionDto.getSolutionText()).build()))
                .status(ChallengeStatus.STARTED)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(any(UUID.class), any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.just(savedDocument));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(savedDocument));

        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getStatus().equals("STARTED") &&
                                userSolutionScoreDto.getUserId().equals(userSolutionDto.getUserId()) &&
                                userSolutionScoreDto.getChallengeId().equals(userSolutionDto.getChallengeId()) &&
                                userSolutionScoreDto.getLanguageId().equals(userSolutionDto.getLanguageId()) &&
                                userSolutionScoreDto.getSolutionText().equals(userSolutionDto.getSolutionText())
                )
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }
    @DisplayName("addSolution returns error when solution status is invalid")
    @Test
    void addSolutionReturnsErrorWhenSolutionStatusIsInvalid() {
        userSolutionDto.setStatus("INVALID_STATUS");

        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().contains("Invalid challenge status value"))
                .verify();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns UnmodifiableSolutionException when status is ENDED")
    @Test
    void addSolutionWithEndedStatus() {
        userSolutionDto.setStatus("ENDED");

        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.ENDED);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

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
    void testAddSolutionWithNullStatus() {
        UserSolutionDto localUserSolutionDto = new UserSolutionDto();
        localUserSolutionDto.setChallengeId("b860f3eb-ef9f-43bf-8c3c-9a5318d26a90");
        localUserSolutionDto.setLanguageId("26cbe8eb-be68-4eb4-96a6-796168e80ec9");
        localUserSolutionDto.setUserId("df99bae8-4f7f-4054-a957-37a12aa16364");
        localUserSolutionDto.setStatus(null); // Set status to null

        Mono<UserSolutionScoreDto> result = userSolutionService.addSolution(localUserSolutionDto);

        StepVerifier.create(result)
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
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());
        ScoreResponseDto scoreResponseDto = new ScoreResponseDto();
        scoreResponseDto.setScore(100);
        scoreResponseDto.setErrors("No errors");

        UserSolutionDocument newUserSolutionDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .solutionDocument(solutionDocuments)
                .status(ChallengeStatus.SENT)
                .score(100)  // Set the score
                .errors("No errors")
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(newUserSolutionDocument));
        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(scoreResponseDto));

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.SENT, solutionDocuments))
                .expectNextMatches(savedDocument -> savedDocument.getScore() == 100 && "No errors".equals(savedDocument.getErrors()))
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }
    @DisplayName("saveValidSolution returns existing solution when status is not modified")
    @Test
    void saveValidSolutionReturnsExistingSolutionWhenStatusIsNotModified() {
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());
        UserSolutionDocument existingSolution = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.STARTED)
                .solutionDocument(solutionDocuments)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingSolution));

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.SENT, solutionDocuments))
                .expectNext(existingSolution)
                .expectComplete()
                .verify();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }
    @DisplayName("saveValidSolution returns empty Mono when no valid status is provided or status is not SENT or STARTED")
    @ParameterizedTest
    @ValueSource(strings = { "EMPTY", "ENDED" })
    void saveValidSolutionReturnsEmptyMonoWhenNoValidStatusOrNotSentOrStarted(String status) {
        List<SolutionDocument> solutionDocuments = List.of(SolutionDocument.builder().solutionText("New solution").build());

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        StepVerifier.create(userSolutionService.saveValidSolution(userUuid, challengeUuid, languageUuid, ChallengeStatus.valueOf(status), solutionDocuments))
                .verifyComplete();

        verify(userSolutionRepository, never()).save(any(UserSolutionDocument.class));
    }
    @DisplayName("getDataFromMicroScore returns valid ScoreResponseDto")
    @Test
    void getDataFromMicroScoreReturnsValidResponse() {
        String localsolutionText = "Sample solution text";
        ScoreResponseDto expectedResponse = new ScoreResponseDto();
        expectedResponse.setScore(100);
        expectedResponse.setErrors("No errors");

        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedResponse));

        CompletableFuture<ScoreResponseDto> resultFuture = userSolutionService.getDataFromMicroScore(challengeUuid, languageUuid, localsolutionText);

        assertNotNull(resultFuture);
        assertEquals(expectedResponse, resultFuture.join());
    }
    @DisplayName("getDataFromMicroScore handles exception and returns default ScoreResponseDto")
    @Test
    void getDataFromMicroScoreHandlesException() {
        String localsolutionText = "Sample solution text";

        when(zmqClient.sendMessage(any(ScoreRequestDto.class), eq(ScoreResponseDto.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("ZMQ error")));

        CompletableFuture<ScoreResponseDto> resultFuture = userSolutionService.getDataFromMicroScore(challengeUuid, languageUuid, localsolutionText);

        assertNotNull(resultFuture);
        ScoreResponseDto result = resultFuture.join();
        assertEquals(0, result.getScore());
        assertNull(result.getErrors());
    }
    @DisplayName("UserSolutionServiceImpTest - showAllUserSolutions returns all solutions for the user")
    @Test
    void showAllUserSolutions() {
        UserSolutionDto localUserSolutionDto = UserSolutionDto.builder()
                .userId(userUuid.toString())
                .challengeId(userSolutionDocument.getChallengeId().toString())
                .languageId(userSolutionDocument.getLanguageId().toString())
                .status(userSolutionDocument.getStatus().toString())
                .solutionText("Sample Solution")
                .build();

        when(userSolutionRepository.findByUserId(userUuid)).thenReturn(Flux.just(userSolutionDocument));
        when(converter.fromUserSolutionDocumentToUserSolutionDto(userSolutionDocument)).thenReturn(Flux.just(localUserSolutionDto));

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
        boolean bookmarked = true;
        UserSolutionDocument existingDocument = UserSolutionDocument.builder()
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .bookmarked(false)
                .build();

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingDocument));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(existingDocument));

        StepVerifier.create(userSolutionService.markAsBookmarked(challengeUuid.toString(), languageUuid.toString(), userUuid.toString(), bookmarked))
                .expectNextMatches(document -> document.isBookmarked() == bookmarked)
                .verifyComplete();

        verify(userSolutionRepository).save(existingDocument);
    }
    @DisplayName("markAsBookmarked creates new document if not found")
    @Test
    void markAsBookmarkedCreatesNewDocumentIfNotFound() {
        boolean bookmarked = true;

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userSolutionService.markAsBookmarked(challengeUuid.toString(), languageUuid.toString(), userUuid.toString(), bookmarked))
                .expectNextMatches(document -> document.isBookmarked() == bookmarked)
                .verifyComplete();

        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }
    @DisplayName("createAndSaveNewBookmark creates and saves new document")
    @Test
    void createAndSaveNewBookmarkCreatesAndSavesNewDocument() {
        boolean bookmarked = true;

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(userSolutionService.createAndSaveNewBookmark(challengeUuid, languageUuid, userUuid, bookmarked))
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

        UserSolutionDocument userSolutionDocument1 = new UserSolutionDocument();
        UserSolutionDocument userSolutionDocument2 = new UserSolutionDocument();
        userSolutionDocument1.setChallengeId(idChallenge);
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
        SolutionDocument solutionDocument1 = new SolutionDocument(UUID.randomUUID(), "solutionText1");
        SolutionDocument solutionDocument2 = new SolutionDocument(UUID.randomUUID(), "solutionText2");
        SolutionDocument solutionDocument3 = new SolutionDocument(UUID.randomUUID(), "solutionText3");
        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2, solutionDocument3);

        UserSolutionDocument userSolutionDoc = new UserSolutionDocument(UUID.randomUUID(), userUuid, challengeUuid, languageUuid, true, ChallengeStatus.STARTED, 1, "x", solutionDocumentList);
        UserScoreDto userScoreDto = new UserScoreDto();
        SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0, 1, 0, new UserScoreDto[]{userScoreDto});

        when(userSolutionRepository.findByUserId(userUuid)).thenReturn(Flux.just(userSolutionDoc));
        when(converter.fromUserScoreDocumentToUserScoreDto(any())).thenReturn(Flux.just(userScoreDto));

        Mono<SolutionUserDto<UserScoreDto>> result = userSolutionService.getChallengeById(userUuid.toString(), challengeUuid.toString(), languageUuid.toString());

        StepVerifier.create(result)
                .expectNextMatches(dto -> Arrays.equals(dto.getResults(), expectedSolutionUserDto.getResults()))
                .expectComplete()
                .verify();
    }
    @DisplayName("getChallengeUsersPercentage returns correct percentage when challenges are found")
    @Test
    void getChallengeUsersPercentageReturnsCorrectPercentageWhenChallengesAreFound() {
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.STARTED)).thenReturn(Flux.just(new UserSolutionDocument()));
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.ENDED)).thenReturn(Flux.just(new UserSolutionDocument()));
        when(userSolutionRepository.findByChallengeId(challengeUuid)).thenReturn(Flux.just(new UserSolutionDocument(), new UserSolutionDocument()));

        Mono<Float> result = userSolutionService.getChallengeUsersPercentage(challengeUuid);

        StepVerifier.create(result)
                .expectNext(100f)
                .expectComplete()
                .verify();
    }
    @DisplayName("getChallengeUsersPercentage returns zero percentage when no challenges are found")
    @Test
    void getChallengeUsersPercentageReturnsZeroPercentageWhenNoChallengesAreFound() {
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.STARTED)).thenReturn(Flux.empty());
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.ENDED)).thenReturn(Flux.empty());
        when(userSolutionRepository.findByChallengeId(challengeUuid)).thenReturn(Flux.just(new UserSolutionDocument(), new UserSolutionDocument()));

        Mono<Float> result = userSolutionService.getChallengeUsersPercentage(challengeUuid);

        StepVerifier.create(result)
                .expectNext(0f)
                .expectComplete()
                .verify();
    }
    @DisplayName("getChallengeUsersPercentage returns error when no challenges exist")
    @Test
    void getChallengeUsersPercentageReturnsErrorWhenNoChallengesExist() {
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.STARTED)).thenReturn(Flux.empty());
        when(userSolutionRepository.findByChallengeIdAndStatus(challengeUuid, ChallengeStatus.ENDED)).thenReturn(Flux.empty());
        when(userSolutionRepository.findByChallengeId(challengeUuid)).thenReturn(Flux.empty());

        Mono<Float> result = userSolutionService.getChallengeUsersPercentage(challengeUuid);

        StepVerifier.create(result)
                .expectError(ChallengeNotFoundException.class)
                .verify();
    }
}