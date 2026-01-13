package com.itachallenge.submission.service;

import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.exception.SubmissionNotFoundException;
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

        Flux<SubmissionDocument> result =
                submissionService.getAllSubmissionsByUser(userId);

        StepVerifier.create(result)
                .assertNext(submission -> {
                    Assertions.assertEquals(userUuid, submission.getUserId());
                    Assertions.assertEquals(challengeUuid, submission.getChallengeId());
                    Assertions.assertEquals(languageUuid, submission.getLanguageId());
                    Assertions.assertEquals(SubmissionStatus.IN_PROGRESS, submission.getStatus());
                    Assertions.assertEquals(submissionText, submission.getSubmissionText());
                })
                .verifyComplete();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsNull() {
        Flux<SubmissionDocument> result =
                submissionService.getAllSubmissionsByUser(null);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof SubmissionNotFoundException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsEmpty() {
        Flux<SubmissionDocument> result =
                submissionService.getAllSubmissionsByUser("   ");
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof SubmissionNotFoundException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsInvalidUuid() {
        String invalid = "not-a-uuid";
        Flux<SubmissionDocument> result =
                submissionService.getAllSubmissionsByUser(invalid);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof SubmissionNotFoundException &&
                                ex.getMessage().contains("must be a valid UUID"))
                .verify();
    }
}
