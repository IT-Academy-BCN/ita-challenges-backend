package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ChallengeControllerTest {
    //region ATTRIBUTES
    @Autowired
    private WebTestClient webTestClient;
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ChallengeService challengeService;

    @Test
    @DisplayName("Test EndPoint: test")
    void TestEndPoint_test(){
        final String URI_TEST = "/test";
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s, equalTo("Hello from ITA Challenge!!!"));
    }

    void testDeleteByResourceID_WithExistingResource_ShouldReturnNoContent() {
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
    void testDeleteByResourceID_WithNonExistingResource_ShouldReturnNotFound() {
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

