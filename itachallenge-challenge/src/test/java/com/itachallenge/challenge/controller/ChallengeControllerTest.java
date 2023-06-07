package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.service.ChallengeService;


import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(ChallengeController.class)
public class ChallengeControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ChallengeService challengeService;

    @Test
    public void testDeleteByResourceID_WithExistingResource_ShouldReturnNoContent() {
        String resourceId = "123";

        when(challengeService.removeResource(resourceId)).thenReturn(true);

        webClient.delete()
                .uri("/itachallenge/api/v1/challenge/resources/{resourceId}", resourceId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(challengeService).removeResource(resourceId);
    }

    @Test
    public void testDeleteByResourceID_WithNonExistingResource_ShouldReturnNotFound() {
        String resourceId = "123";

        when(challengeService.removeResource(resourceId)).thenReturn(false);

        webClient.delete()
                .uri("/itachallenge/api/v1/challenge/resources/{resourceId}", resourceId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(challengeService).removeResource(resourceId);
    }
}

