package com.itachallenge.submission.service;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.repository.SubmissionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceImplTest {

    @Mock
    private SubmissionRepository submissionRepository;

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
}
