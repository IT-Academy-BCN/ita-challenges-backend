package com.itachallenge.jwtcore.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

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

    @Test
    void getUserUuIdFromAuthenticationHeader_validToken_returnsUuid() {
        String uuid = "uuid-1234";
        String token = jwtService.generateToken("testuser", "USER", uuid);
        String authHeader = "Bearer " + token;

        String result = jwtService.getUserUuIdFromAuthenticationHeader(authHeader);

        assertThat(result).isEqualTo(uuid);
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_nullHeader_throwsJwtException() {
        assertThatThrownBy(() -> jwtService.getUserUuIdFromAuthenticationHeader(null))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Missing or bad formatted Authorization header");
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_malformedHeader_throwsJwtException() {
        assertThatThrownBy(() -> jwtService.getUserUuIdFromAuthenticationHeader("Token something"))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Missing or bad formatted Authorization header");
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_invalidToken_throwsJwtException() {
        String invalidToken = "Bearer invalid.token.value";
        assertThatThrownBy(() -> jwtService.getUserUuIdFromAuthenticationHeader(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid Authorization header content");
    }

    @Test
    void extractUuid_invalidToken_returnsNull() {
        String result = invokeExtractUuid("invalid.token.structure");
        assertThat(result).isNull();
    }

    // Acceso al método privado mediante reflexión
    private String invokeExtractUuid(String token) {
        try {
            var method = JwtService.class.getDeclaredMethod("extractUuid", String.class);
            method.setAccessible(true);
            return (String) method.invoke(jwtService, token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void extractAllClaims_validToken_returnsClaims() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String token = jwtService.generateToken("testuser", "USER", "uuid-9876");
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims).isNotNull();
        assertThat(claims.get("sub")).isEqualTo("testuser");
        assertThat(claims.get("role")).isEqualTo("USER");
        assertThat(claims.get("uuid")).isEqualTo("uuid-9876");
    }

    @Test
    void extractAllClaims_invalidToken_throwsException() {
        String invalidToken = "malformed.token.string"; // An invalid JWT string

        assertThatExceptionOfType(JwtException.class)
                .isThrownBy(() -> jwtService.extractAllClaims(invalidToken))
                .withMessageContaining("Invalid or tampered token");
    }


}
