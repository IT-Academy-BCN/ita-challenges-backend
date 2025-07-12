package com.itachallenge.user.service;

import com.itachallenge.user.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Test
    @DisplayName("Test: Extract role from a valid token")
    void extractRoleFromToken_validToken_shouldReturnRole() {
        String validToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.Y2v_G_DRdM5sA3i6aD6gS-35s2t6qk-i4n_m-h3a8E";

        Mono<String> result = jwtService.extractRoleFromToken(validToken);

        StepVerifier.create(result)
                .expectNext("ADMIN")
                .verifyComplete();
    }

    @Test
    @DisplayName("Test: Attempt to extract role with invalid header")
    void extractRoleFromToken_invalidHeader_shouldReturnError() {
        String invalidToken = "this.is.not.a.valid.bearer.token";

        Mono<String> result = jwtService.extractRoleFromToken(invalidToken);

        StepVerifier.create(result)
                .expectError(UnauthorizedException.class)
                .verify();
    }

    @Test
    @DisplayName("Test: Attempt to extract role from a malformed token")
    void extractRoleFromToken_malformedToken_shouldReturnError() {
        String malformedToken = "Bearer not-a-jwt";

        Mono<String> result = jwtService.extractRoleFromToken(malformedToken);

        StepVerifier.create(result)
                .expectError(UnauthorizedException.class)
                .verify();
    }

    @Test
    @DisplayName("Test: Attempt to extract role from a token without role claim")
    void extractRoleFromToken_missingRoleClaim_shouldReturnError() {
        String tokenWithoutRole = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        Mono<String> result = jwtService.extractRoleFromToken(tokenWithoutRole);

        StepVerifier.create(result)
                .expectError(UnauthorizedException.class)
                .verify();
    }
}