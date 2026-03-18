package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionAction;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.repository.SubmissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.itachallenge.challenge.dto.SolvedDto;
import org.mockito.ArgumentCaptor;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private IChallengeService challengeService;
    @Mock
    private IChallengeJwtFacade challengeJwtFacade;

    private SubmissionServiceImpl submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionServiceImpl(
                submissionRepository,
                challengeService,
                challengeJwtFacade
        );
    }

    @Test
    void getAllSubmissionsByUser_shouldReturnSubmissionDocuments() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        String userId = userUuid.toString();
        String submissionText = "Hello World!!";

        SubmissionDocument document = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(SubmissionStatus.IN_PROGRESS)
                .submissionText(submissionText)
                .build();

        when(submissionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.just(document));

        Flux<SubmissionDto> result =
                submissionService.getAllSubmissionsByUser(userId);

        StepVerifier.create(result)
                .assertNext(submission -> {
                    Assertions.assertEquals(userUuid.toString(), submission.getUserId());
                    Assertions.assertEquals(challengeUuid.toString(), submission.getChallengeId());
                    Assertions.assertEquals(languageUuid.toString(), submission.getLanguageId());
                    Assertions.assertEquals(SubmissionStatus.IN_PROGRESS.name(), submission.getStatus());
                    Assertions.assertEquals(submissionText, submission.getSubmissionText());
                })
                .verifyComplete();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsNull() {
        StepVerifier.create(submissionService.getAllSubmissionsByUser(null))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("userId") &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsEmpty() {
        StepVerifier.create(submissionService.getAllSubmissionsByUser("   "))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("userId") &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsInvalidUuid() {
        StepVerifier.create(submissionService.getAllSubmissionsByUser("not-a-uuid"))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("userId") &&
                                ex.getMessage().contains("must be a valid UUID"))
                .verify();
    }

    @Test
    void createOrUpdateSubmission_shouldCreateInProgress_whenActionIsSave() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("draft text")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .assertNext(response -> {
                    Assertions.assertEquals("draft text", response.getSubmissionText());
                    Assertions.assertEquals(SubmissionStatus.IN_PROGRESS.name(), response.getStatus());
                    Assertions.assertFalse(response.getIsSolved());
                    Assertions.assertNull(response.getTimesSolved());
                })
                .verifyComplete();

        verify(challengeService, never()).addChallengeToSolved(anyString());
    }

    @Test
    void createOrUpdateSubmission_shouldSubmitComplete_andIncrementSolved() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionDocument existing = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(SubmissionStatus.IN_PROGRESS)
                .submissionText("draft")
                .build();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SUBMIT)
                .submissionText("final")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existing));

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(challengeJwtFacade.getUsernameFromAuthenticationHeader(any())).thenReturn(null);

        when(challengeService.addChallengeToSolved(anyString()))
                .thenReturn(Mono.just(new SolvedDto(true, 3)));

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .assertNext(response -> {
                    Assertions.assertEquals(SubmissionStatus.SUBMITTED_COMPLETE.name(), response.getStatus(),
                            "status");
                    Assertions.assertTrue(response.getIsSolved(), "isSolved");
                    Assertions.assertNotNull(response.getTimesSolved(), "timesSolved should be set from addChallengeToSolved");
                    Assertions.assertEquals(3, response.getTimesSolved(),
                            "timesSolved: expected 3 from addChallengeToSolved mock, got " + response.getTimesSolved());

                })
                .verifyComplete();

        verify(challengeService).addChallengeToSolved(anyString());
    }

    @Test
    void createOrUpdateSubmission_shouldThrow_whenAlreadySubmitted() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionDocument existing = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(SubmissionStatus.SUBMITTED_COMPLETE)
                .submissionText("done")
                .build();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("try change")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existing));

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .expectError(UnmodifiableSubmissionException.class)
                .verify();

        verify(challengeService, never()).addChallengeToSolved(anyString());
    }

    @Test
    void createOrUpdateSubmission_shouldAllowEmptySubmissionText_whenActionIsSave() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("   ")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .assertNext(response -> Assertions.assertEquals(SubmissionStatus.IN_PROGRESS.name(), response.getStatus()))
                .verifyComplete();
    }
    @Test
    void processSubmissionAction_shouldThrow_whenActionIsSubmitAndSubmissionTextIsBlank() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SUBMIT)
                .submissionText("   ")
                .build();

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException
                                && ex.getMessage().contains("submissionText")
                                && ex.getMessage().contains("cannot be blank"))
                .verify();

        verify(submissionRepository, never()).save(any(SubmissionDocument.class));
    }

    @Test
    void processSubmissionAction_shouldThrow_whenActionIsSubmitAndSubmissionTextIsNull() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SUBMIT)
                .submissionText(null)
                .build();

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException
                                && ex.getMessage().contains("submissionText")
                                && ex.getMessage().contains("cannot be blank"))
                .verify();

        verify(submissionRepository, never()).save(any(SubmissionDocument.class));
    }

    @Test
    void processSubmissionAction_shouldStoreSubmittedByUsername_whenAuthHeaderProvided() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();
        String authHeader = "Bearer token";
        String username = "alice";

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("draft")
                .build();

        when(challengeJwtFacade.getUsernameFromAuthenticationHeader(authHeader)).thenReturn(username);
        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());
        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, authHeader))
                .assertNext(response -> Assertions.assertEquals(SubmissionStatus.IN_PROGRESS.name(), response.getStatus()))
                .verifyComplete();

        ArgumentCaptor<SubmissionDocument> captor = ArgumentCaptor.forClass(SubmissionDocument.class);
        verify(submissionRepository).save(captor.capture());
        Assertions.assertEquals(username, captor.getValue().getSubmittedByUsername());
    }
    @Test
    void getPeerSubmissions_whenUserHasSubmitted_returnsPeerSubmissionsOrderedByDateDesc() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        when(submissionRepository.existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList()))
                .thenReturn(Mono.just(true));

        SubmissionDocument doc = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(otherUserId)
                .challengeId(challengeId)
                .languageId(UUID.randomUUID())
                .status(SubmissionStatus.SUBMITTED_COMPLETE)
                .submissionText("solution code")
                .createdAt(LocalDateTime.now().minusDays(1))
                .submittedByUsername("peerUser")
                .build();

        when(submissionRepository.findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                eq(challengeId), eq(userId), anyList()))
                .thenReturn(Flux.just(doc));

        Flux<PeerSubmissionItemDto> result = submissionService.getPeerSubmissions(challengeId, userId);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getChallengeId().equals(challengeId.toString())
                                && dto.getUserId().equals(otherUserId.toString())
                                && "solution code".equals(dto.getSubmissionText())
                                && dto.getStatus().equals(SubmissionStatus.SUBMITTED_COMPLETE.name())
                                && "peerUser".equals(dto.getAuthor()))
                .verifyComplete();

        verify(submissionRepository).existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList());
        verify(submissionRepository).findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                eq(challengeId), eq(userId), anyList());
    }

    @Test
    void getPeerSubmissions_whenUserHasNotSubmitted_returns403() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(submissionRepository.existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList()))
                .thenReturn(Mono.just(false));

        Flux<PeerSubmissionItemDto> result = submissionService.getPeerSubmissions(challengeId, userId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResponseStatusException
                                && ((ResponseStatusException) throwable).getStatusCode().value() == 403)
                .verify();

        verify(submissionRepository).existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList());
        verify(submissionRepository, never())
                .findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(any(), any(), anyList());
    }

    @Test
    void getPeerSubmissions_whenUserHasSubmitted_returnsEmptyList_whenNoPeerSubmissions() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(submissionRepository.existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList()))
                .thenReturn(Mono.just(true));
        when(submissionRepository.findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                eq(challengeId), eq(userId), anyList()))
                .thenReturn(Flux.empty());

        Flux<PeerSubmissionItemDto> result = submissionService.getPeerSubmissions(challengeId, userId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(submissionRepository).existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList());
        verify(submissionRepository).findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                eq(challengeId), eq(userId), anyList());
    }

    @Test
    void getPeerSubmissions_whenDocumentHasNoAuthor_returnsDtoWithNullAuthor() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        when(submissionRepository.existsByUserIdAndChallengeIdAndStatusIn(eq(userId), eq(challengeId), anyList()))
                .thenReturn(Mono.just(true));

        SubmissionDocument doc = SubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(otherUserId)
                .challengeId(challengeId)
                .languageId(UUID.randomUUID())
                .status(SubmissionStatus.SUBMITTED_COMPLETE)
                .submissionText("solution code")
                .createdAt(LocalDateTime.now().minusDays(1))
                .submittedByUsername(null)
                .build();

        when(submissionRepository.findTop10ByChallengeIdAndUserIdNotAndStatusInOrderByCreatedAtDesc(
                eq(challengeId), eq(userId), anyList()))
                .thenReturn(Flux.just(doc));

        Flux<PeerSubmissionItemDto> result = submissionService.getPeerSubmissions(challengeId, userId);

        StepVerifier.create(result)
                .expectNextMatches(dto ->
                        dto.getChallengeId().equals(challengeId.toString())
                                && dto.getUserId().equals(otherUserId.toString())
                                && "solution code".equals(dto.getSubmissionText())
                                && dto.getAuthor() == null)
                .verifyComplete();
    }

    @Test
    void processSubmissionAction_shouldThrow_whenChallengeIdIsNull() {
        UUID userUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(null)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("draft")
                .build();

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException
                                && ex.getMessage().contains("challengeId")
                                && ex.getMessage().contains("cannot be null"))
                .verify();

        verify(submissionRepository, never()).save(any(SubmissionDocument.class));
    }

    @Test
    void processSubmissionAction_shouldThrow_whenLanguageIdIsNull() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(null)
                .action(SubmissionAction.SAVE)
                .submissionText("draft")
                .build();

        StepVerifier.create(submissionService.processSubmissionAction(userUuid.toString(), request, null))
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException
                                && ex.getMessage().contains("languageId")
                                && ex.getMessage().contains("cannot be null"))
                .verify();

        verify(submissionRepository, never()).save(any(SubmissionDocument.class));
    }

}
