package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.UnmodifiableSolutionException;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.mockito.InjectMocks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImpTest {

    @Mock
    IUserSolutionRepository userSolutionRepository;
    @InjectMocks
    UserSolutionServiceImp userSolutionService;

    private String solutionText;
    private UUID userUuid;
    private UUID challengeUuid;
    private UUID languageUuid;
    private int mockScore;
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
                .solutionDocument(List.of(SolutionDocument.builder().solutionText(solutionText).build()))
                .score(mockScore).build();

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
        assert(userSolutionDocument.isBookmarked());
        assert (userSolutionDocument.getUserId().equals(userId));
        assert (userSolutionDocument.getLanguageId().equals(languageId));
        assert (userSolutionDocument.getChallengeId().equals(challengeId));

    }

    @DisplayName("UserSolutionServiceImpTest - addSolution creates a new document when existing document can't be found and status is empty or ENDED")
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"ENDED"})
    void addSolutionNewSolutionWithEmptyStatus_test(String status) {
        userSolutionDocument.setStatus(null);
        userSolutionDto.setStatus(status);

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getUserId().equals(userUuid.toString())
                                && userSolutionScoreDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionScoreDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionScoreDto.getSolutionText().equals(solutionText)
                                && userSolutionScoreDto.getScore() == mockScore)
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution modifies an existing document when status is empty")
    @Test
    void addSolutionModifySolutionWithEmptyStatus_test() {
        userSolutionDocument.setStatus(null);
        userSolutionDto.setStatus(null);

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getUserId().equals(userUuid.toString())
                                && userSolutionScoreDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionScoreDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionScoreDto.getSolutionText().equals(solutionText)
                                && userSolutionScoreDto.getScore() == 13)
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution saves a solution when new status is ENDED and solution wasn't already ENDED")
    @Test
    void addSolutionWithEndedStatusWhenValid_test() {
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.STARTED);

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getUserId().equals(userUuid.toString())
                                && userSolutionScoreDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionScoreDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionScoreDto.getSolutionText().equals(solutionText)
                                && userSolutionScoreDto.getScore() == 13)
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns EndedChallengeException when solution's status was previously set to ENDED")
    @Test
    void addSolutionWithEndedStatusWhenInvalid_test() {
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.ENDED);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

        StepVerifier.create(userSolutionService.addSolution(userSolutionDto))
                .expectErrorMatches(
                        throwable -> throwable instanceof UnmodifiableSolutionException
                        && throwable.getMessage().equals("Invalid challenge status: status was already ENDED")).verify();
        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns IllegalArgumentException when provided status is STARTED, EMPTY or invalid")
    @ParameterizedTest
    @ValueSource(strings = {"STARTED", "EMPTY", "InvalidStatus"})
    void addSolutionWithInvalidStatus_test(String status) {
        userSolutionDto.setStatus(status);

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(userSolutionDto);

        StepVerifier.create(resultMono)
            .expectErrorMatches(
                    throwable -> throwable instanceof IllegalArgumentException
                        && throwable.getMessage().equals("Invalid challenge status: " + status)).verify();
        verifyNoInteractions(userSolutionRepository);

    }

}