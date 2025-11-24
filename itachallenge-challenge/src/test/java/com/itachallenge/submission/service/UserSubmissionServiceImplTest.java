package com.itachallenge.submission.service;

/*import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.submission.document.SubmissionAttemptDocument;
import com.itachallenge.submission.document.UserSubmissionDocument;
import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import com.itachallenge.submission.enums.ChallengeStatus;
import com.itachallenge.submission.repository.IUserSubmissionRepository;
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
class UserSubmissionServiceImplTest {

    @Mock
    private IUserSubmissionRepository userSubmissionRepository;

    @InjectMocks
    private UserSubmissionServiceImpl userSubmissionService;

    @Test
    void getAllSubmissionsByUser_shouldReturnMappedDtos() {
        UUID userUuid = UUID.randomUUID();
        UUID challengeUuid = UUID.randomUUID();
        UUID languageUuid = UUID.randomUUID();

        String userId = userUuid.toString();

        SubmissionAttemptDocument attempt = SubmissionAttemptDocument.builder()
                .uuid(UUID.randomUUID())
                .submissionText("my submission text")
                .build();
        UserSubmissionDocument document = UserSubmissionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(ChallengeStatus.SUBMITTED_INCOMPLETE)
                .submissionAttemptDocument(attempt)
                .build();
        when(userSubmissionRepository.findAllByUserId(userUuid))
                .thenReturn(Flux.just(document));
        Flux<UserSubmissionResponseDto> result =
                userSubmissionService.getAllSubmissionsByUser(userId);
        StepVerifier.create(result)
                .assertNext(dto -> {
                    org.junit.jupiter.api.Assertions.assertEquals(userId, dto.getUserId());
                    org.junit.jupiter.api.Assertions.assertEquals(challengeUuid.toString(), dto.getChallengeId());
                    org.junit.jupiter.api.Assertions.assertEquals(languageUuid.toString(), dto.getLanguageId());
                    org.junit.jupiter.api.Assertions.assertEquals("my submission text", dto.getSolutionText());
                    org.junit.jupiter.api.Assertions.assertEquals("SUCCESS", dto.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsNull() {
        Flux<UserSubmissionResponseDto> result =
                userSubmissionService.getAllSubmissionsByUser(null);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsEmpty() {
        Flux<UserSubmissionResponseDto> result =
                userSubmissionService.getAllSubmissionsByUser("   ");
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("cannot be null or empty"))
                .verify();
    }

    @Test
    void getAllSubmissionsByUser_shouldThrow_whenUserIdIsInvalidUuid() {
        String invalid = "not-a-uuid";
        Flux<UserSubmissionResponseDto> result =
                userSubmissionService.getAllSubmissionsByUser(invalid);
        StepVerifier.create(result)
                .expectErrorMatches(ex ->
                        ex instanceof BadRequestException &&
                                ex.getMessage().contains("must be a valid UUID"))
                .verify();
    }
}*/

