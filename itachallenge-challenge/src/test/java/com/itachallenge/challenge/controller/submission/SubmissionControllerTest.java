package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.SubmissionActionRequestDto;
import com.itachallenge.challenge.dto.submission.SubmissionActionResponseDto;
import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.common.exception.GlobalExceptionHandler;
import com.itachallenge.submission.enums.SubmissionAction;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;
import com.itachallenge.submission.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(MockitoExtension.class)
class SubmissionControllerTest {

    @Mock
    SubmissionService submissionService;

    @InjectMocks
    SubmissionController submissionController;

    private WebTestClient client() {
        return WebTestClient.bindToController(submissionController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllSubmissions_returnsSubmissions() {
        String userId = UUID.randomUUID().toString();

        SubmissionDto s1 = SubmissionDto.builder()
                .userId(userId)
                .challengeId("d43a1a4d-ee8f-432d-8f9c-68eda2547dae")
                .languageId("409c9fe8-74de-4db3-81a1-a55280cf92ef")
                .submissionText("This is the submitted solution")
                .status("IN_PROGRESS")
                .build();

        SubmissionDto s2 = SubmissionDto.builder()
                .userId(userId)
                .challengeId("b5c06903-f27b-4057-8220-ad9d957cdce4")
                .languageId("09fabe32-7362-4bfb-ac05-b7bf854c6e0f")
                .submissionText("This is the submitted solution")
                .status("IN_PROGRESS")
                .build();

        when(submissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.just(s1, s2));

        client().get()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SubmissionDto.class)
                .hasSize(2)
                .value(list -> {
                    assertEquals(s1.getChallengeId(), list.get(0).getChallengeId());
                    assertEquals(s2.getChallengeId(), list.get(1).getChallengeId());
                });

        verify(submissionService).getAllSubmissionsByUser(userId);
    }

    @Test
    void getAllSubmissions_returns200WithEmptyArrayWhenNoSubmissions() {
        String userId = UUID.randomUUID().toString();

        when(submissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.empty());

        client().get()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SubmissionDto.class)
                .hasSize(0);

        verify(submissionService).getAllSubmissionsByUser(userId);
    }
    @Test
    void getAllSubmissions_returns400WhenUserIdIsMalformed_byService() {
        String badId = "not-a-uuid";
        when(submissionService.getAllSubmissionsByUser(badId))
                .thenReturn(Flux.error(new BadUUIDException("Invalid UUID")));

        client().get()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", badId)
                .exchange()
                .expectStatus().isBadRequest();

        verify(submissionService).getAllSubmissionsByUser(badId);
    }

    @Test
    void getAllSubmissions_returns500IfUnexpectedError() {
        String userId = UUID.randomUUID().toString();

        when(submissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.error(new RuntimeException("Boom")));

        client().get()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(submissionService).getAllSubmissionsByUser(userId);
    }
    @Test
    void postSubmission_returns200_whenServiceSucceeds() {
        String userId = UUID.randomUUID().toString();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .languageId(UUID.randomUUID())
                .action(SubmissionAction.SAVE)
                .submissionText("draft text")
                .build();

        SubmissionActionResponseDto response = SubmissionActionResponseDto.builder()
                .submissionText("draft text")
                .isSolved(false)
                .timesSolved(0)
                .status("IN_PROGRESS")
                .build();

        when(submissionService.processSubmissionAction(eq(userId), any(SubmissionActionRequestDto.class),any()))
                .thenReturn(Mono.just(response));

        client().post()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.submission_text").isEqualTo("draft text")
                .jsonPath("$.status").isEqualTo("IN_PROGRESS");

        verify(submissionService).processSubmissionAction(eq(userId), any(SubmissionActionRequestDto.class), any());
    }

    @Test
    void postSubmission_returns400_whenRequestIsInvalid() {
        String userId = UUID.randomUUID().toString();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(null)
                .languageId(UUID.randomUUID())
                .action(SubmissionAction.SAVE)
                .submissionText("draft text")
                .build();

        client().post()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();

        verify(submissionService, never())
                .processSubmissionAction(any(), any(), any());
    }


    @Test
    void postSubmission_returns409_whenSubmissionIsUnmodifiable() {
        String userId = UUID.randomUUID().toString();

        SubmissionActionRequestDto request = SubmissionActionRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .languageId(UUID.randomUUID())
                .action(SubmissionAction.SAVE)
                .submissionText("draft text")
                .build();


        when(submissionService.processSubmissionAction(eq(userId), any(SubmissionActionRequestDto.class), any()))
                .thenReturn(Mono.error(new UnmodifiableSubmissionException("Submission already completed")));

        client().post()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409);

        verify(submissionService).processSubmissionAction(eq(userId), any(SubmissionActionRequestDto.class), any());
    }

    @Test
    void postSubmission_returns400_whenBodyContainsInvalidUUID() {
        String userId = UUID.randomUUID().toString();

        String invalidJson = """
        {
          "uuid_challenge": "not-a-uuid",
          "uuid_language": "also-not-a-uuid",
          "action": "SAVE",
          "submission_text": "draft text"
        }
        """;

        client().post()
                .uri("/itachallenge/api/v1/users/{userId}/submissions", userId)
                .header("Content-Type", "application/json")
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest();

        verify(submissionService, never())
                .processSubmissionAction(any(), any(), any());
    }

}