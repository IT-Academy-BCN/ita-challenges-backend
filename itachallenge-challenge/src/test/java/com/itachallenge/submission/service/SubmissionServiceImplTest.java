package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.dto.submission.SubmissionRequestDto;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionAction;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.repository.SubmissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import com.itachallenge.challenge.dto.SolvedDto;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.UUID;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private IChallengeService challengeService;



    @InjectMocks
    private SubmissionServiceImpl submissionService;

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

        SubmissionRequestDto request = SubmissionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("draft text")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionService.createOrUpdateSubmission(userUuid.toString(), request))
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

        SubmissionRequestDto request = SubmissionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SUBMIT)
                .submissionText("final")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existing));

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        when(challengeService.addChallengeToSolved(challengeUuid.toString()))
                .thenReturn(Mono.just(new SolvedDto(true, 3)));

        StepVerifier.create(submissionService.createOrUpdateSubmission(userUuid.toString(), request))
                .assertNext(response -> {
                    Assertions.assertEquals(SubmissionStatus.SUBMITTED_COMPLETE.name(), response.getStatus());
                    Assertions.assertTrue(response.getIsSolved());
                    Assertions.assertEquals(3, response.getTimesSolved());
                })
                .verifyComplete();

        verify(challengeService).addChallengeToSolved(challengeUuid.toString());
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

        SubmissionRequestDto request = SubmissionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("try change")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.just(existing));

        StepVerifier.create(submissionService.createOrUpdateSubmission(userUuid.toString(), request))
                .expectError(UnmodifiableSubmissionException.class)
                .verify();

        verify(challengeService, never()).addChallengeToSolved(anyString());
    }

    @Test
    void createOrUpdateSubmission_shouldAllowEmptySubmissionText_whenActionIsSave() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        SubmissionRequestDto request = SubmissionRequestDto.builder()
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .action(SubmissionAction.SAVE)
                .submissionText("   ")
                .build();

        when(submissionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid))
                .thenReturn(Mono.empty());

        when(submissionRepository.save(any(SubmissionDocument.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(submissionService.createOrUpdateSubmission(userUuid.toString(), request))
                .assertNext(response -> Assertions.assertEquals(SubmissionStatus.IN_PROGRESS.name(), response.getStatus()))
                .verifyComplete();
    }

}
