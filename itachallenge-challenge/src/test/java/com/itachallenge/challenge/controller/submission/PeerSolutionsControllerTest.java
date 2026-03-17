package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.dto.submission.PeerSubmissionItemDto;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
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

@WebFluxTest(PeerSolutionsController.class)
class PeerSolutionsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SubmissionService submissionService;

    @MockBean
    private IChallengeJwtFacade challengeJwtFacade;

    @Test
    void getPeerSolutions_whenUserHasSubmitted_returns200WithList() {
        UUID challengeId = UUID.randomUUID();
        UUID userUuid = UUID.randomUUID();
        String authHeader = "Bearer token";
        LocalDateTime submittedAt = LocalDateTime.now().minusDays(1);

        PeerSubmissionItemDto item = PeerSubmissionItemDto.builder()
                .submissionId(UUID.randomUUID().toString())
                .challengeId(challengeId.toString())
                .userId(UUID.randomUUID().toString())
                .submissionText("code")
                .status("SUBMITTED_COMPLETE")
                .submittedAt(submittedAt)
                .build();

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userUuid.toString());
        when(submissionService.getPeerSolutions(eq(challengeId), eq(userUuid))).thenReturn(Flux.just(item));

        webTestClient.get()
                .uri("/itachallenge/api/v1/submission/challenge/{challengeId}/peer-solutions", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].submission_id").isNotEmpty()
                .jsonPath("$[0].challenge_id").isEqualTo(challengeId.toString())
                .jsonPath("$[0].submission_text").isEqualTo("code");

        verify(submissionService).getPeerSolutions(eq(challengeId), eq(userUuid));
    }

    @Test
    void getPeerSolutions_invalidChallengeId_returns400() {
        String authHeader = "Bearer token";

        webTestClient.get()
                .uri("/itachallenge/api/v1/submission/challenge/not-a-uuid/peer-solutions")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(submissionService);
    }

    @Test
    void getPeerSolutions_missingAuthHeader_returns400() {
        UUID challengeId = UUID.randomUUID();

        webTestClient.get()
                .uri("/itachallenge/api/v1/submission/challenge/{challengeId}/peer-solutions", challengeId)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(submissionService);
    }
}

