package com.itachallenge.submission.service;

import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.submission.document.UserSubmissionDocument;
import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.enums.SubmissionStatus;
import com.itachallenge.submission.repository.SubmissionRepository;
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
    void getAllSubmissionsByUser_shouldReturnMappedDtos() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        String userId = userUuid.toString();
        String submissionText = "Hello World!!";

        UserSubmissionDocument document = UserSubmissionDocument.builder()
                .submissionId(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(SubmissionStatus.IN_PROGRESS)
                .submissionText(submissionText)
                .build();
        when(submissionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.just(document));
        Flux<SubmissionResponseDto> result =
                submissionService.getAllSubmissionsByUser(userId);
        StepVerifier.create(result)
                .assertNext(dto -> {
                    org.junit.jupiter.api.Assertions.assertEquals(userId, dto.getUserId());
                    org.junit.jupiter.api.Assertions.assertEquals(challengeUuid.toString(), dto.getChallengeId());
                    org.junit.jupiter.api.Assertions.assertEquals(languageUuid.toString(), dto.getLanguageId());
                    org.junit.jupiter.api.Assertions.assertEquals("Hello World!!", dto.getSubmissionText());
                    org.junit.jupiter.api.Assertions.assertEquals("IN_PROGRESS", dto.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsNull() {
        Flux<SubmissionResponseDto> result =
                submissionService.getAllSubmissionsByUser(null);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsEmpty() {
        Flux<SubmissionResponseDto> result =
                submissionService.getAllSubmissionsByUser("   ");
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsInvalidUuid() {
        String invalid = "not-a-uuid";
        Flux<SubmissionResponseDto> result =
                submissionService.getAllSubmissionsByUser(invalid);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("must be a valid UUID"))
                .verify();
    }
}
