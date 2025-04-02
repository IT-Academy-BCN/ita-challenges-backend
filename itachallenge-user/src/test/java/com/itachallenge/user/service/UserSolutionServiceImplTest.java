package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.*;
import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.exception.UnmodificableSolutionException;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserSolutionServiceImplTest {

    @Mock
    IUserSolutionRepository userSolutionRepository;

    @InjectMocks
    UserSolutionServiceImpl userSolutionService;

    private String solutionText;
    private UUID userUuid;
    private UUID challengeUuid;
    private UUID languageUuid;
    private UserSolutionRequestDto userSolutionRequestDto;
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
        userSolutionRequestDto = UserSolutionRequestDto.builder()
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
                .build();

    }

    @DisplayName("UserSolutionServiceImpTest - addSolution creates a new document when existing document can't be found and status is 'ENDED'")
    @Test
    void addSolutionNewSolutionWithStatusEnded_test() {

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        Mono<UserSolutionResponseDto> resultMono = userSolutionService.addSolution(userSolutionRequestDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionResponseDto ->
                        userSolutionResponseDto.getUserId().equals(userUuid.toString())
                                && userSolutionResponseDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionResponseDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionResponseDto.getSolutionText().equals(solutionText))
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution throws IllegalArgumentException when new status is empty")
    @Test
    void addSolutionModifySolutionWithEmptyStatus_test() {
        userSolutionDocument.setStatus(null);
        userSolutionRequestDto.setStatus(null);

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        Mono<UserSolutionResponseDto> resultMono = userSolutionService.addSolution(userSolutionRequestDto);

        StepVerifier.create(resultMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof IllegalArgumentException
                                && throwable.getMessage().equals("Status not allowed")).verify();
        verifyNoInteractions(userSolutionRepository);
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution saves a solution when new status is ENDED and existing solution had status null")
    @Test
    void addSolutionWithEndedStatusWhenExistingSolutionExistsValid_test() {
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(null);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(
                any(UUID.class), any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.just(existingUserSolutionDocument));
        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));


        Mono<UserSolutionResponseDto> resultMono = userSolutionService.addSolution(userSolutionRequestDto);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionResponseDto ->
                        userSolutionResponseDto.getUserId().equals(userUuid.toString())
                                && userSolutionResponseDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionResponseDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionResponseDto.getSolutionText().equals(solutionText))
                .verifyComplete();
        verify(userSolutionRepository).save(any(UserSolutionDocument.class));
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns UnmodificableSolutionException when existing solution's status is already 'ENDED'")
    @Test
    void addSolutionWithEndedStatusWhenInvalid_test() {
        UserSolutionDocument existingUserSolutionDocument = userSolutionDocument;
        existingUserSolutionDocument.setStatus(ChallengeStatus.ENDED);

        when(userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existingUserSolutionDocument));

        StepVerifier.create(userSolutionService.addSolution(userSolutionRequestDto))
                .expectErrorMatches(
                        throwable -> throwable instanceof UnmodificableSolutionException
                                && throwable.getMessage().equals("Existing solution has status ENDED, and thus cannot be modified.")).verify();
        verify(userSolutionRepository).findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid);
        verifyNoMoreInteractions(userSolutionRepository);
    }

    @DisplayName("UserSolutionServiceImpTest - addSolution returns IllegalArgumentException when provided status is not 'ENDED'")
    @ParameterizedTest
    @ValueSource(strings = {"", "InvalidStatus"})
    void addSolutionWithInvalidStatus_test(String status) {
        userSolutionRequestDto.setStatus(status);

        Mono<UserSolutionResponseDto> resultMono = userSolutionService.addSolution(userSolutionRequestDto);

        StepVerifier.create(resultMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof IllegalArgumentException
                                && throwable.getMessage().equals("Status not allowed")).verify();
        verifyNoInteractions(userSolutionRepository);

    }
}


