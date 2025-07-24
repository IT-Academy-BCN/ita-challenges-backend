package com.itachallenge.auth.controller;

import com.itachallenge.auth.config.TestAuthConfig;
import com.itachallenge.auth.dto.SwitchRoleRequest;
import com.itachallenge.auth.dto.User;
import com.itachallenge.auth.service.JwtRoleSwitchService;
import com.itachallenge.auth.exception.InvalidRoleChangeRequestException;
import com.itachallenge.auth.service.IAuthService;
import com.itachallenge.auth.service.IAuthJwtFacade;
import com.itachallenge.auth.service.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@WebFluxTest(AuthController.class)
@TestPropertySource(properties = "token.expiration.minutes=60")
@Import(TestAuthConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    Environment env;

    @MockBean
    private IAuthService authService;

    @MockBean
    private JwtRoleSwitchService jwtRoleSwitchService;

    @MockBean
    private IUserService userService;

    @InjectMocks
    private AuthController authController;

    @MockBean
    private IAuthJwtFacade authJwtFacade;

    @Test
    void authenticateWithGithub_ValidCode_ReturnsJwt() {
        String validCode = "valid-code";
        String accessToken = "valid-token";
        String githubUsername = "octocat";
        User user = new User("1234", githubUsername, "ADMIN");
        String jwtToken = "generatedJwt";

        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);
        validationResult.put("token", jwtToken);

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));
        when(userService.fetchUserData(githubUsername)).thenReturn(Mono.just(user));
        when(authJwtFacade.generateToken(user.getUsername(), user.getRole(), user.getUuid())).thenReturn(jwtToken);

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
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
        String accessToken = "invalid-token";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", false);
        validationResult.put("username", null);

        when(authService.exchangeCodeForToken(invalidCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", invalidCode))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenExchangeError_ReturnsInternalServerError() {
        String invalidCode = "invalid-code";

        when(authService.exchangeCodeForToken(invalidCode))
                .thenReturn(Mono.error(new RuntimeException("Token exchange failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", invalidCode))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_TokenValidationError_ReturnsInternalServerError() {
        String validCode = "valid-code";
        String accessToken = "valid-token";

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken))
                .thenReturn(Mono.error(new RuntimeException("Token validation failed")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").isEmpty();
    }

    @Test
    void authenticateWithGithub_UserDoesNotExist_ReturnsForbidden() {
        String validCode = "valid-code";
        String accessToken = "valid-token";
        String githubUsername = "octocat";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));
        when(userService.fetchUserData(githubUsername)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("User does not exist in the database")
                .jsonPath("$.username").doesNotExist()
                .jsonPath("$.token").doesNotExist();
    }

    @Test
    void authenticateWithGithub_UserValidationError_ReturnsInternalServerError() {
        String validCode = "valid-code";
        String accessToken = "valid-token";
        String githubUsername = "octocat";
        Map<String, Object> validationResult = new HashMap<>();
        validationResult.put("isValid", true);
        validationResult.put("username", githubUsername);

        when(authService.exchangeCodeForToken(validCode)).thenReturn(Mono.just(accessToken));
        when(authService.validateTokenWithGithub(accessToken)).thenReturn(Mono.just(validationResult));
        when(userService.fetchUserData(githubUsername)).thenReturn(Mono.error(new RuntimeException("Database error")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/github/authenticate")
                .bodyValue(Map.of("code", validCode))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.isValid").isEqualTo(false)
                .jsonPath("$.username").doesNotExist()
                .jsonPath("$.token").doesNotExist();
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
        Mockito.doNothing().when(authJwtFacade).validateToken(token);
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
                .when(authJwtFacade).validateToken(expiredToken);

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
        Mockito.doNothing().when(authJwtFacade).validateToken(token);

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
                .when(authJwtFacade).validateToken(invalidToken);

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

    @Test
    void switchRole_WithValidTokenAndValidRole_ReturnsNewToken() {
        String originalToken = "valid.jwt.token";
        String newRole = "ADMIN";
        String newToken = "new.jwt.token";

        when(authJwtFacade.extractBearerToken("Bearer " + originalToken)).thenReturn(originalToken);
        when(jwtRoleSwitchService.switchRole(originalToken, newRole)).thenReturn(newToken);

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/switch-role")
                .header("Authorization", "Bearer " + originalToken)
                .bodyValue(new SwitchRoleRequest(newRole))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isEqualTo(newToken);
    }

    @Test
    void switchRole_WithExpiredToken_Returns200WithMessage() {
        String expiredToken = "expired.jwt.token";
        String newRole = "ADMIN";

        when(authJwtFacade.extractBearerToken("Bearer " + expiredToken)).thenReturn(expiredToken);
        when(jwtRoleSwitchService.switchRole(expiredToken, newRole))
                .thenThrow(new ExpiredJwtException(null, null, "Token is expired."));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/switch-role")
                .header("Authorization", "Bearer " + expiredToken)
                .bodyValue(new SwitchRoleRequest(newRole))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Token is expired.");
    }

    @Test
    void switchRole_WithInvalidToken_Returns401() {
        String invalidToken = "invalid.jwt.token";
        String newRole = "ADMIN";

        when(authJwtFacade.extractBearerToken("Bearer " + invalidToken)).thenReturn(invalidToken);
        when(jwtRoleSwitchService.switchRole(invalidToken, newRole))
                .thenThrow(new JwtException("Invalid or tampered token."));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/switch-role")
                .header("Authorization", "Bearer " + invalidToken)
                .bodyValue(new SwitchRoleRequest(newRole))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").value(msg -> assertThat(msg).isEqualTo("Invalid or tampered token."));
    }

    @Test
    void switchRole_WithSameRole_Throws400() {
        String token = "valid.jwt.token";

        when(authJwtFacade.extractBearerToken("Bearer " + token)).thenReturn(token);
        when(jwtRoleSwitchService.switchRole(token, "USER"))
                .thenThrow(new InvalidRoleChangeRequestException("New role is the same as current role."));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/switch-role")
                .header("Authorization", "Bearer " + token)
                .bodyValue(new SwitchRoleRequest("USER"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("New role is the same as current role.");
    }

    @Test
    void switchRole_MissingAuthorizationHeader_Returns401() {
        when(authJwtFacade.extractBearerToken(null))
                .thenThrow(new JwtException("Authorization header is missing or malformed"));

        webTestClient.post()
                .uri("/itachallenge/api/v1/auth/switch-role")
                .bodyValue(new SwitchRoleRequest("ADMIN"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Authorization header is missing or malformed");
    }
}
