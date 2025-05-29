package com.itachallenge.auth.controller;

import com.itachallenge.auth.exception.CustomBadRequestException;
import com.itachallenge.auth.dto.User;
import com.itachallenge.auth.exception.CustomInternalServerErrorException;
import com.itachallenge.auth.service.IAuthService;
import com.itachallenge.auth.service.IJwtService;
import com.itachallenge.auth.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(AuthController.class)
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    Environment env;

    @MockBean
    private IAuthService authService;

    @MockBean
    private IUserService userService;

    @MockBean
    private IJwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testEndpoint_ReturnsGreetingMessage() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/auth/test") // ajusta el path si es necesario
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello from ITA ChallengeAuth!!!");
    }

    @Test
    void authenticateWithGithub_ValidCode_ReturnsJwt() {
        String validCode = "valid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        String accessToken = "valid-token";
        String githubUsername = "octocat";
        User user = new User("1234", githubUsername, "ADMIN");
        String jwtToken = "generatedJwt";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);
        validationResult.put("token", jwtToken);

        when(authService.exchangeCodeForToken(validCode, redirectUri)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));
        when(userService.fetchUserData(githubUsername)).thenReturn(Mono.just(user));
        when(jwtService.generateToken(user.getUsername(), user.getRole(), user.getUuid())).thenReturn(jwtToken);
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", validCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(true)
                .jsonPath("$.username").isEqualTo(githubUsername)
                .jsonPath("$.token").isEqualTo(jwtToken);
    }

    @Test
    void authenticateWithGithub_InvalidCode_ReturnsUnauthorized() {
        String invalidCode = "invalid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        String accessToken = "invalid-token";

        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", false);
        validationResult.put("username", null);

        when(authService.exchangeCodeForToken(invalidCode, redirectUri)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", invalidCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenExchangeError_ReturnsInternalServerError() {
        String invalidCode = "invalid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";

        when(authService.exchangeCodeForToken(invalidCode, redirectUri))
                .thenReturn(Mono.error(new RuntimeException("Token exchange failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", invalidCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenValidationError_ReturnsInternalServerError() {
        String validCode = "valid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        String accessToken = "valid-token";

        when(authService.exchangeCodeForToken(validCode, redirectUri)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken))
                .thenReturn(Mono.error(new RuntimeException("Token validation failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", validCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_UserDoesNotExist_ReturnsForbidden() {
        String validCode = "valid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        String accessToken = "valid-token";
        String githubUsername = "octocat";

        // Este map debe tener "isValid": true para NO entrar en el flujo que lanza 401
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true); // ✅ importante que sea true
        validationResult.put("username", githubUsername);

        when(authService.exchangeCodeForToken(eq(validCode), eq(redirectUri)))
                .thenReturn(Mono.just(accessToken));

        when(authService.validateTokenWithGithub(eq(accessToken)))
                .thenReturn(Mono.just(validationResult));

        when(userService.fetchUserData(eq(githubUsername)))
                .thenReturn(Mono.empty()); // ⛔ usuario no existe → activa switchIfEmpty

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", validCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().isForbidden() // ✔️ esperando 403
                .expectHeader().valueEquals("X-Validation-Status", "Forbidden")
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.message").value(msg -> assertThat(msg.toString()).contains("User does not exist"))
                .jsonPath("$.username").isEqualTo(null)
                .jsonPath("$.token").doesNotExist();
    }

    @Test
    void givenCustomBadRequestException_thenHandledGracefully() {
        Mono<String> testMono = Mono.<String>error(new CustomBadRequestException("Invalid input"))
                .onErrorResume(throwable -> {
                    if (throwable instanceof CustomBadRequestException) {
                        return Mono.just("Handled custom bad request");
                    }
                    return Mono.error(throwable);
                });

        StepVerifier.create(testMono)
                .expectNext("Handled custom bad request")
                .verifyComplete();
    }

    @Test
    void authenticateWithGithub_UserValidationError_ReturnsInternalServerError() {
        String validCode = "valid-code";
        String redirectUri = "http://localhost:4200/ita-challenge/challenges";
        String accessToken = "valid-token";
        String githubUsername = "octocat";

        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);

        when(authService.exchangeCodeForToken(validCode, redirectUri)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));
        when(userService.fetchUserData(githubUsername)).thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("code", validCode, "redirect_uri", redirectUri))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").doesNotExist()
                .jsonPath("$.token").doesNotExist();
    }

    @Test
    void onErrorResume_WithOtherException_NotCustomBadRequest() {
        Mono<String> mono = Mono.<String>error(new RuntimeException("Some error"))
                .onErrorResume(throwable -> {
                    String message;
                    if (throwable instanceof CustomBadRequestException) {
                        message = "Bad request: " + throwable.getMessage();
                    } else if (throwable instanceof CustomInternalServerErrorException) {
                        message = throwable.getMessage();
                    } else {
                        message = "Unknown error";
                    }
                    return Mono.just("Handled: " + message);
                });

        StepVerifier.create(mono)
                .expectNext("Handled: Unknown error")
                .verifyComplete();
    }

    @Test
    void authenticateWithGithub_EmptyBody_ReturnsBadRequest() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")  // cuerpo JSON vacío válido
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Missing 'code' or 'redirectUri' in the request")
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEqualTo(null);
    }

    @Test
    void givenCustomInternalServerErrorException_thenHandledGracefully() {
        Mono<String> testMono = Mono.<String>error(new CustomInternalServerErrorException("Server crashed"))
                .onErrorResume(throwable -> {
                    String message;
                    if (throwable instanceof CustomBadRequestException) {
                        message = "Bad request: " + throwable.getMessage();
                    } else if (throwable instanceof CustomInternalServerErrorException) {
                        message = throwable.getMessage();  // <-- esta línea se cubrirá
                    } else {
                        message = "Unknown error";
                    }
                    return Mono.just("Handled: " + message);
                });

        StepVerifier.create(testMono)
                .expectNext("Handled: Server crashed")
                .verifyComplete();
    }

    @Test
    void givenCustomInternalServerErrorExceptionWithNullMessage_thenHandledGracefully() {
        Mono<String> testMono = Mono.<String>error(new CustomInternalServerErrorException(null))
                .onErrorResume(throwable -> {
                    String message;
                    if (throwable instanceof CustomBadRequestException) {
                        message = "Bad request: " + throwable.getMessage();
                    } else if (throwable instanceof CustomInternalServerErrorException) {
                        message = throwable.getMessage(); // puede ser null aquí
                    } else {
                        message = "Unknown error";
                    }
                    return Mono.just("Handled: " + message);
                });

        StepVerifier.create(testMono)
                .expectNext("Handled: null")
                .verifyComplete();
    }

    @Test
    void callUserTest_ReturnsExpectedString() {
        String expectedResponse = "User service test response";

        // Mockeamos el comportamiento del servicio
        when(userService.callUserTest()).thenReturn(Mono.just(expectedResponse));

        webTestClient.get()
                .uri("/itachallenge/api/v1/auth/call-user-test") // ajusta la ruta según tu controlador
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedResponse);
    }

    @Test
    void authenticateWithGithub_MissingRequestBody_ReturnsBadRequest() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getVersionTest() {
        String expectedVersion = env.getProperty("spring.application.version");

        assert expectedVersion != null;

        webTestClient.get()
                .uri("/itachallenge/api/v1/auth/version")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.application_name").isEqualTo("itachallenge-auth")
                .jsonPath("$.version").isEqualTo(expectedVersion);
    }

    @Test
    void logout_ValidToken_ShouldReturn200() {
        String token = "valid.jwt.token";
        Mockito.doNothing().when(jwtService).validateToken(token);
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> assertThat(response.get("message")).isEqualTo("Logout successful"));
    }

    @Test
    void logout_ExpiredToken_ShouldReturn200() {
        String expiredToken = "expired.jwt.token";
        Mockito.doThrow(new ExpiredJwtException(null, null, "Token expired but logout successful"))
                .when(jwtService).validateToken(expiredToken);

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "Bearer " + expiredToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> {
                    assertThat(response.get("message")).isEqualTo("Token expired but logout successful");
                });
    }

    @Test
    void logout_TokenJustWithinTry_ShouldReturn200() {
        String token = "any.jwt.token";
        Mockito.doNothing().when(jwtService).validateToken(token);

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> assertThat(response.get("message")).isEqualTo("Logout successful"));
    }

    @Test
    void logout_InvalidToken_ShouldReturn401() {
        String invalidToken = "invalid.jwt.token";
        Mockito.doThrow(new JwtException("Invalid or tampered token: JWT parsing failed"))
                .when(jwtService).validateToken(invalidToken);

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "Bearer " + invalidToken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .value(response -> {
                    assertThat(response.get("message").toString()).startsWith("Invalid or tampered token");
                });
    }

    @Test
    void logout_EmptyAuthorizationHeader_ShouldReturn401() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.get("message").equals("Authorization header is missing or malformed");
                });
    }

   @Test
    void logout_MalformedAuthorizationHeader_ShouldReturn401() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .header("Authorization", "Token xyz")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.get("message").equals("Authorization header is missing or malformed");
                });
    }

    @Test
    void logout_MissingAuthorizationHeader_ShouldReturn401() {
        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/logout")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.get("message").equals("Authorization header is missing or malformed");
                });
    }
}