package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.exception.ResourceNotFoundException;
import com.itachallenge.challenge.repository.*;
import com.itachallenge.challenge.service.IResourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ResourceControllerExceptionPathTest
 *
 * Purpose:
 * - Covers ONLY the unhappy/exceptional flows from the original ResourceControllerTest.
 * - The “happy path” tests remain covered separately in a WebFlux-based suite.
 * - This split is required because of the hybrid nature of the itachallenge-challenge microservice,
 *   which mixes reactive (Netty) and servlet (Tomcat) stacks.
 * - These tests run under Tomcat (@WebMvcTest) but use WebTestClient bound to MockMvc internally.
 */
@WebMvcTest(controllers = ResourceController.class)
@ComponentScan(basePackages = {
        "com.itachallenge.errorcore",
        "com.itachallenge.challenge.exception"
})
@ActiveProfiles("test")
class ResourceControllerExceptionPathTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MessageSource messageSource;

    // --- mocked dependencies ---
    @MockBean private IResourceService resourceService;
    @MockBean private DiscoveryClient discoveryClient;
    @MockBean private ChallengeRepository challengeRepository;
    @MockBean private SolutionRepository solutionRepository;
    @MockBean private WebClient.Builder webClientBuilder;
    @MockBean private TagRepository tagRepository;
    @MockBean private ResourceRepository resourceRepository;
    @MockBean private MappingMongoConverter mappingMongoConverter;
    @MockBean private LanguageRepository languageRepository;

    // -------------------------------------------------------------------------
    // Exception Path Tests
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("POST /resource/new with invalid JSON → 400 Bad Request (Malformed request body)")
    void createResource_InvalidJson_Returns400() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"title\": }")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("Invalid"));

        verify(resourceService, never()).createResource(any());
    }

    @Test
    @DisplayName("POST /resource/new with missing required field → 400 (validation.argument_not_valid)")
    void createResource_MissingRequiredField_Returns400() {
        String invalidJson = """
                {
                  "resourceId": "11111111-1111-1111-1111-111111111111",
                  "title": "",
                  "description": "Some description",
                  "url": "https://example.com",
                  "topic": "DEBUGGING",
                  "contentType": "VIDEO",
                  "challengeIds": ["11111111-1111-1111-1111-111111111111"],
                  "associationType": "NONE"
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"resourceDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "resource.title.notEmpty",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/resource/new")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));

        verify(resourceService, never()).createResource(any());
    }

    @Test
    @DisplayName("GET /resource/challenge/{id} with INVALID UUID → 400 Bad Request (BadUUIDException)")
    void getResourcesByChallengeId_InvalidId_Returns400() {
        String invalidId = "not-a-uuid";

        webTestClient.get()
                .uri("/itachallenge/api/v1/resource/challenge/{id}", invalidId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("invalid"));

        verify(resourceService, never()).getResourcesByChallengeId(any());
    }

    @Test
    @DisplayName("GET /resource/challenge/{id} → ResourceNotFoundException → 404 Not Found")
    void getResourcesByChallengeId_NotFound_Returns404() {
        UUID challengeId = UUID.randomUUID();
        String errorMessage = "No resources found for challenge ID: " + challengeId;

        when(resourceService.getResourcesByChallengeId(challengeId))
                .thenReturn(Flux.error(new ResourceNotFoundException(errorMessage)));

        webTestClient.get()
                .uri("/itachallenge/api/v1/resource/challenge/{id}", challengeId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo(errorMessage);

        verify(resourceService).getResourcesByChallengeId(challengeId);
    }

    @Test
    @DisplayName("GET /resource/challenge/{id} → InternalServerErrorException → 500")
    void getResourcesByChallengeId_InternalError_Returns500() {
        UUID challengeId = UUID.randomUUID();
        String errorMessage = "Unexpected database error";

        when(resourceService.getResourcesByChallengeId(challengeId))
                .thenReturn(Flux.error(new InternalServerErrorException(errorMessage)));

        webTestClient.get()
                .uri("/itachallenge/api/v1/resource/challenge/{id}", challengeId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("Unexpected exception in service layer → 500 Internal Server Error (BaseExceptionHandler.handleAny)")
    void genericUnhandledException_Returns500() {
        UUID challengeId = UUID.randomUUID();

        when(resourceService.getResourcesByChallengeId(challengeId))
                .thenReturn(Flux.error(new RuntimeException("Unexpected boom")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/resource/challenge/{id}", challengeId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message").value(containsString("Unexpected"));
    }
}
