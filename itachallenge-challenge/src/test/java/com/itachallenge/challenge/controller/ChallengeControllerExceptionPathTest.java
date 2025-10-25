package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.ChallengeCreateDto;
import com.itachallenge.challenge.exception.*;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import reactor.core.publisher.Mono;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ChallengeControllerExceptionTest
 * Purpose:
 * - This suite covers ONLY the unhappy/exceptional flows from the original ChallengeControllerTest.
 *   The “happy path” tests are covered separately in the WebFlux-based suite.
 * - This split is recommended because of the hybrid system in place in itachallenge-challenge, mixing blocking and reactive stacks.
 */
@WebMvcTest(controllers = ChallengeController.class)
@ComponentScan(basePackages = {
        "com.itachallenge.errorcore",           // BaseExceptionHandler, ErrorMessageConfig, etc.
        "com.itachallenge.challenge.exception"  // If you have ChallengeExceptionHandler extending BaseExceptionHandler
})
@ActiveProfiles("test")
class ChallengeControllerExceptionTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MessageSource messageSource;

    // --- mocked collaborators the controller depends on ---
    @MockBean private IChallengeService challengeService;
    @MockBean private ITagService tagService;
    @MockBean private DiscoveryClient discoveryClient;
    @MockBean private PropertiesConfig config;
    @MockBean private IChallengeJwtFacade challengeJwtFacade;
    @MockBean private ChallengeRepository challengeRepository;
    @MockBean private ILanguageService languageService;
    @MockBean private IResourceService resourceService;
    @MockBean private IUserService userService;
    @MockBean private MappingMongoConverter mappingMongoConverter;

    //For parametrized test
    static Stream<TestCase> invalidUpdateCases() {
        return Stream.of(
                new TestCase(
                        "Empty challengeTitle",
                        """
                        {
                          "challengeTitle": "",
                          "description": "desc",
                          "level": "EASY",
                          "language": "Java",
                          "solution": "solution",
                          "topic": "LISTS",
                          "tags": ["11111111-1111-1111-1111-111111111111"]
                        }
                        """,
                        "challenge.title.notEmpty"
                ),
                new TestCase(
                        "Empty description",
                        """
                        {
                          "challengeTitle": "Title",
                          "description": "",
                          "level": "EASY",
                          "language": "Java",
                          "solution": "solution",
                          "topic": "LISTS",
                          "tags": ["11111111-1111-1111-1111-111111111111"]
                        }
                        """,
                        "challenge.description.notEmpty"
                ),
                new TestCase(
                        "Empty language",
                        """
                        {
                          "challengeTitle": "Title",
                          "description": "Desc",
                          "level": "EASY",
                          "language": "",
                          "solution": "solution",
                          "topic": "LISTS",
                          "tags": ["11111111-1111-1111-1111-111111111111"]
                        }
                        """,
                        "challenge.language.notEmpty"
                ),
                new TestCase(
                        "Null solution",
                        """
                        {
                          "challengeTitle": "Title",
                          "description": "Desc",
                          "level": "EASY",
                          "language": "Java",
                          "solution": null,
                          "topic": "LISTS",
                          "tags": ["11111111-1111-1111-1111-111111111111"]
                        }
                        """,
                        "challenge.solution.notEmpty"
                ),
                new TestCase(
                        "Null topic",
                        """
                        {
                          "challengeTitle": "Title",
                          "description": "Desc",
                          "level": "EASY",
                          "language": "Java",
                          "solution": "solution",
                          "topic": null,
                          "tags": ["11111111-1111-1111-1111-111111111111"]
                        }
                        """,
                        "challenge.topic.notNull"
                )
        );
    }

    private record TestCase(String display, String body, String messageKey) {}



    @Test
    @DisplayName("GET /challenges/{id}/related with INVALID UUID → 400 Bad Request (BadUUIDException from service)")
    void getRelatedChallenges_InvalidUUID_Returns400() {
        String invalidUuid = "not-a-valid-uuid";

        when(challengeService.getRelatedChallenges(invalidUuid))
                .thenReturn(Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format.")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/related", invalidUuid)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Invalid ID format. Please indicate the correct format.");
    }

    @Test
    @DisplayName("GET /challenges/{id}/related where service throws ChallengeNotFoundException → 404 Not Found")
    void getRelatedChallenges_NotFound_Returns404() {
        String uuid = UUID.randomUUID().toString();

        when(challengeService.getRelatedChallenges(uuid))
                .thenReturn(Mono.error(new ChallengeNotFoundException("Challenge not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/related", uuid)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Challenge not found");
    }

    @Test
    @DisplayName("POST /solution with null solutionText → 400 (MethodArgumentNotValidException)")
    void addSolution_NullSolutionText_ThrowsBadRequest() {

        String body = """
                {
                  "solution_text": null,
                  "uuid_challenge": "11111111-1111-1111-1111-111111111111",
                  "uuid_language": "11111111-1111-1111-1111-111111111111"
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"solutionDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "solution.text.notEmpty",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("POST /solution with empty solutionText ('') → 400 (MethodArgumentNotValidException)")
    void addSolution_EmptySolutionText_ThrowsBadRequest() {
        String body = """
                {
                  "solution_text": "",
                  "uuid_challenge": "11111111-1111-1111-1111-111111111111",
                  "uuid_language": "11111111-1111-1111-1111-111111111111"
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"solutionDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "solution.text.notEmpty",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("POST /solution with null challengeId → 400 (MethodArgumentNotValidException)")
    void addSolution_NullChallengeId_ThrowsBadRequest() {

        String body = """
                {
                  "solution_text": "valid text",
                  "uuid_challenge": null,
                  "uuid_language": "11111111-1111-1111-1111-111111111111"
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"solutionDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "solution.challengeId.invalid",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("POST /solution with null languageId → 400 (MethodArgumentNotValidException)")
    void addSolution_NullLanguageId_ThrowsBadRequest() {

        String body = """
                {
                  "solution_text": "valid text",
                  "uuid_challenge": "11111111-1111-1111-1111-111111111111",
                  "uuid_language": null
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"solutionDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "solution.languageId.invalid",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("POST /solution with {} empty body → 400 (MethodArgumentNotValidException)")
    void addSolution_NullSolutionDto_ThrowsBadRequest() {
        String body = "{}";

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"solutionDto"},
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                // no strict field assertion because multiple fields are missing
                .jsonPath("$.errors[0].message").exists();
    }

    @Test
    @DisplayName("POST /challenges with empty required field challengeTitle → 400 (bean validation)")
    void addChallenge_EmptyField_statusBadRequest() {

        String body = """
                {
                  "challengeTitle": "",
                  "description": "descripció",
                  "level": "EASY",
                  "language": "Java",
                  "solution": "solució",
                  "topic": "COMPONENTS",
                  "tags": ["11111111-1111-1111-1111-111111111111"]
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"challengeCreateDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "challenge.title.notEmpty",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("POST /challenges with invalid language at service layer → 400 (BadRequestException)")
    void addChallenge_InvalidLanguage_statusBadRequest() {
        String authHeader = "Bearer valid-token";
        String userId = "user123";

        String body = """
                {
                  "challengeTitle": "títol",
                  "description": "descripció",
                  "level": "EASY",
                  "language": "InvalidLanguage",
                  "solution": "solució",
                  "topic": "LISTS",
                  "tags": ["11111111-1111-1111-1111-111111111111"]
                }
                """;

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);

        when(challengeService.addChallenge(any(ChallengeCreateDto.class)))
                .thenReturn(Mono.error(new BadRequestException("Invalid language")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Invalid language");
    }

    @Test
    @DisplayName("POST /challenges with invalid enum in body (level='TOUGH') → 400 (HttpMessageNotReadableException → validation.bad_request)")
    void addChallenge_InvalidLevel_statusBadRequest() {
        String body = """
                {
                  "challengeTitle": "títol",
                  "description": "descripció",
                  "level": "TOUGH",
                  "language": "Java",
                  "solution": "solució",
                  "topic": "LISTS",
                  "tags": ["11111111-1111-1111-1111-111111111111"]
                }
                """;

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("TOUGH"));
    }

    @Test
    @DisplayName("POST /challenges with empty tags [] → 400 (challenge.tags.notEmpty)")
    void addChallenge_EmptyTags_statusBadRequest() {
        String body = """
                {
                  "challengeTitle": "title",
                  "description": "description",
                  "level": "EASY",
                  "language": "Java",
                  "solution": "solution",
                  "topic": "ALL",
                  "tags": []
                }
                """;

        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"challengeCreateDto"},
                Locale.getDefault()
        );
        String expectedFieldMessage = messageSource.getMessage(
                "challenge.tags.notEmpty",
                null,
                Locale.getDefault()
        );

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("DELETE /challenges/{id} where service throws ChallengeNotFoundException → 404 Not Found")
    void deleteOneChallenge_notFound() {
        String id = "non_existing_id";

        when(challengeService.deleteChallengeById(id))
                .thenReturn(Mono.error(new ChallengeNotFoundException(
                        String.format("Challenge with id: %s not found", id))));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Challenge with id: non_existing_id not found");
    }

    @Test
    @DisplayName("POST /challenges/{id}/bookmarks → ChallengeNotFoundException → 404")
    void addChallengeToBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String authHeader = "validAuthHeader";
        String userId = "existing_userId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);

        when(challengeService.addChallengeToBookmarks(challengeId, userId))
                .thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("POST /challenges/{id}/bookmarks → InternalServerErrorException → 500")
    void addChallengeToBookmarks_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String authHeader = "validAuthHeader";
        String userId = "existing_userId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);

        when(challengeService.addChallengeToBookmarks(challengeId, userId))
                .thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("POST /challenges/{id}/bookmarks with invalid Authorization header → JwtException → 400")
    void addChallengeToBookmarks_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String badHeader = "badHeader";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(badHeader))
                .thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", badHeader)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("POST /challenges/{id}/bookmarks with MISSING Authorization header → JwtException → 400")
    void addChallengeToBookmarks_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(null))
                .thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                // no header
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("PUT /challenge/{id}/update with MISSING Authorization header → 400 (JwtException mapped to BadRequestException)")
    void updateChallenge_MissingAuthHeader_Returns400() {
        String challengeId = "some_id";
        String errorMessage = "Missing auth header";

        // jwtFacade called with null -> throw JwtException
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(null))
                .thenThrow(new JwtException(errorMessage));

        // Body is otherwise valid
        String body = """
                {
                  "challengeTitle": "títol",
                  "description": "descripció",
                  "level": "EASY",
                  "language": "Java",
                  "solution": "solució",
                  "topic": "LISTS",
                  "tags": ["11111111-1111-1111-1111-111111111111"]
                }
                """;

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/{challengeId}/update", challengeId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @ParameterizedTest(name = "PUT /challenge/update → 400 when {0}")
    @MethodSource("invalidUpdateCases")
    @DisplayName("PUT /challenge/{id}/update with invalid field → 400 (MethodArgumentNotValidException)")
    void updateChallenge_InvalidField_Returns400(TestCase testCase) {
        String expectedTopMessage = messageSource.getMessage(
                "validation.argument_not_valid",
                new Object[]{"challengeCreateDto"},
                Locale.getDefault()
        );

        String expectedFieldMessage = messageSource.getMessage(
                testCase.messageKey(),
                null,
                Locale.getDefault()
        );

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/{challengeId}/update", "any-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testCase.body())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo(expectedTopMessage)
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errors[0].message").value(containsString(expectedFieldMessage));
    }

    @Test
    @DisplayName("PUT /challenge/{id}/update with invalid level enum → 400 (validation.bad_request)")
    void updateChallenge_InvalidLevelEnum_Returns400() {
        String body = """
                {
                  "challengeTitle": "Title",
                  "description": "Description",
                  "level": "invalidLevel",
                  "language": "Java",
                  "solution": "valid solution",
                  "topic": "ALL",
                  "tags": {}
                }
                """;

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/{challengeId}/update", "some-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("invalidLevel"));
    }

    @Test
    @DisplayName("PUT /challenge/{id}/update with invalid topic enum → 400 (validation.bad_request)")
    void updateChallenge_InvalidTopicEnum_Returns400() {

        String body = """
                {
                  "challengeTitle": "Title",
                  "description": "Description",
                  "level": "EASY",
                  "language": "Java",
                  "solution": "valid solution",
                  "topic": "invalidTopic",
                  "tags": {}
                }
                """;

        webTestClient.put()
                .uri("/itachallenge/api/v1/challenge/challenge/{challengeId}/update", "some-id")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").value(containsString("invalidTopic"));
    }

    @Test
    @DisplayName("DELETE /challenges/{id}/bookmarks → ChallengeNotFoundException → 404")
    void removeChallengeFromBookmarks_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String authHeader = "validAuthHeader";
        String userId = "existing_userId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId))
                .thenReturn(Mono.error(new ChallengeNotFoundException(errorMessage)));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("DELETE /challenges/{id}/bookmarks → InternalServerErrorException → 500")
    void removeChallengeFromBookmarks_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String authHeader = "validAuthHeader";
        String userId = "existing_userId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);

        when(challengeService.removeChallengeFromBookmarks(challengeId, userId))
                .thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.status").isEqualTo(500)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("DELETE /challenges/{id}/bookmarks with invalid Authorization header → JwtException → 400")
    void removeChallengeFromBookmarks_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String badHeader = "BadHeader";
        String errorMessage = "Error message";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(badHeader))
                .thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                .header("Authorization", badHeader)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    @Test
    @DisplayName("DELETE /challenges/{id}/bookmarks with MISSING Authorization header → 400 (JwtException mapped to BadRequestException)")
    void removeChallengeFromBookmarks_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(null))
                .thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/challenge/challenges/{challengeId}/bookmarks", challengeId)
                // no header
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo(errorMessage);
    }

    // -------------------------------------------------------------------------
    // getChallengesByFilter() unhappy paths (binding / validation issues)
    //
    // In the original "unhappy path" group we did not assert anything specific
    // for /challenges/byFilter because the negative cases there were mostly
    // about bad/missing params, malformed UUID, etc. The controller method:
    //
    //   public Flux<GenericResultDto<ChallengeDto>> getChallengesByFilter(@ModelAttribute ChallengeFilterDto filter)
    //
    // In your previous final breakdown for unhappy-path tests we did not include
    // a dedicated failing getChallengesByFilter() case, so we're not adding
    // a new test here. If you later want to assert 400 on malformed params,
    // you can replicate the same "invalid enum" / "bad UUID" strategy:
    // mock service to emit BadRequestException / BadUUIDException and assert 400.
    // -------------------------------------------------------------------------

}
