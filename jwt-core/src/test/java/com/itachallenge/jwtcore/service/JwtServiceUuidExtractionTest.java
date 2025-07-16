package com.itachallenge.jwtcore.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceUuidExtractionTest {

    private JwtService jwtService;

    private final String jwtSigningKey = "bXlTZWNyZXRTaWduaW5nS2V5V2hpY2hJc1ZlcnlTZWN1cmVBbmRub2JvZHlDb3VsZEd1ZXNz";
    private final long minutesTillExpiration = 10L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtSigningKey, minutesTillExpiration);
    }

    @Test
    void getUserUuidFromValidAuthorizationHeader_ShouldReturnUuid() {
        String uuid = "test-uuid-123";
        String token = jwtService.generateToken("testUser", "USER", uuid);
        String authHeader = "Bearer " + token;

        Claims claims = jwtService.extractAllClaims(jwtService.extractBearerToken(authHeader));
        String extractedUuid = claims.get("uuid", String.class);

        assertThat(extractedUuid).isEqualTo(uuid);
    }

    @Test
    void extractBearerToken_WithNullHeader_ShouldThrowException() {
        assertThatThrownBy(() -> jwtService.extractBearerToken(null))
                .isInstanceOf(JwtException.class)
                .hasMessage("Authorization header is missing or malformed");
    }

    @Test
    void extractBearerToken_WithMalformedHeader_ShouldThrowException() {
        String malformedHeader = "Token xyz.abc.def";
        assertThatThrownBy(() -> jwtService.extractBearerToken(malformedHeader))
                .isInstanceOf(JwtException.class)
                .hasMessage("Authorization header is missing or malformed");
    }

    @Test
    void extractAllClaims_WithInvalidToken_ShouldThrowJwtException() {
        String invalidToken = "invalid.token.here";
        assertThatThrownBy(() -> jwtService.extractAllClaims(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }
}
