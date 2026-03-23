package com.itachallenge.challenge.controller.peer;

import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.submission.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@WebFluxTest(com.itachallenge.challenge.controller.peer.ChallengePeerSubmissionsController.class)
class ChallengePeerSubmissionsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private IChallengeJwtFacade challengeJwtFacade;

    @Test
    void getPeerSubmissions_whenUserHasSubmitted_returns200WithList() {
        UUID challengeId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        String authHeader = "Bearer token";
        LocalDateTime submittedAt = LocalDateTime.now().minusDays(1);

        PeerSubmissionItemDto item = PeerSubmissionItemDto.builder()
                .submissionId(UUID.randomUUID().toString())
                .submissionText("code")
                .status("SUBMITTED_COMPLETE")
                .submittedAt(submittedAt)
                .build();

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userUuid.toString());
        when(submissionService.getPeerSubmissions(challengeId, userUuid.toString()))
                .thenReturn(Flux.just(item));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].submission_id").isNotEmpty()
                .jsonPath("$[0].submission_text").isEqualTo("code");

        verify(submissionService).getPeerSubmissions(challengeId, userUuid.toString());
    }

    @Test
    void getPeerSubmissions_invalidChallengeId_returns400() {
        String authHeader = "Bearer token";

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/not-a-uuid/peer-submissions")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(submissionService);
    }

    @Test
    void getPeerSubmissions_missingAuthHeader_returns400() {
        UUID challengeId = UUID.randomUUID();

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(submissionService);
    }

    @Test
    void getPeerSubmissions_whenFacadeThrowsJwtException_returns400() {
        UUID challengeId = UUID.randomUUID();
        String authHeader = "Bearer token";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenThrow(new io.jsonwebtoken.JwtException("Missing or invalid authorization"));
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isBadRequest();
        verifyNoInteractions(submissionService);
    }
    @Test
    void getPeerSubmissions_whenUserIdFromJwtIsInvalidUuid_returns400() {
        UUID challengeId = UUID.randomUUID();
        String authHeader = "Bearer token";
        String invalidUserId = "not-a-uuid";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(invalidUserId);
        when(submissionService.getPeerSubmissions(challengeId, invalidUserId))
                .thenReturn(Flux.error(new BadRequestException("Invalid UUID for userId.")));
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isBadRequest();
        verify(submissionService).getPeerSubmissions(challengeId, invalidUserId);
    }
    @Test
    void getPeerSubmissions_whenUserHasNotSubmitted_returns403() {
        UUID challengeId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        String authHeader = "Bearer token";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userUuid.toString());
        when(submissionService.getPeerSubmissions(challengeId, userUuid.toString()))
                .thenReturn(Flux.error(new com.itachallenge.common.exception.ForbiddenException(
                        "Access denied to requested resource"
                )));
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isForbidden();
        verify(submissionService).getPeerSubmissions(challengeId, userUuid.toString());
    }
    @Test
    void getPeerSubmissions_whenServiceThrowsUnexpectedError_returns500() {
        UUID challengeId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        String authHeader = "Bearer token";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userUuid.toString());
        when(submissionService.getPeerSubmissions(challengeId, userUuid.toString()))
                .thenReturn(Flux.error(new RuntimeException("boom")));
        webTestClient.get()
                .uri("/itachallenge/api/v1/challenges/{challengeId}/peer-submissions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().is5xxServerError();
        verify(submissionService).getPeerSubmissions(challengeId, userUuid.toString());
    }
}

