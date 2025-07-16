package com.itachallenge.jwtcore.service;

import com.itachallenge.jwtcore.exception.InvalidRoleChangeRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    // Clave en Base64, válida para HMAC-SHA con al menos 256 bits
    private final String jwtSigningKey = "bXlTZWNyZXRTaWduaW5nS2V5V2hpY2hJc1ZlcnlTZWN1cmVBbmRub2JvZHlDb3VsZEd1ZXNz";
    private final long minutesTillExpiration = 10L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtSigningKey, minutesTillExpiration);
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        String username = "testUser";
        String role = "ADMIN";
        String uuid = "uuid";

        String token = jwtService.generateToken(username, role, uuid);

        assertThat(token).isNotNull().isNotEmpty();

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSigningKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isEqualTo(role);
        assertThat(claims.get("uuid", String.class)).isEqualTo(uuid);
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void generateToken_ShouldHaveCorrectExpirationTime() {
        String username = "testUser";
        String role = "USER";
        String uuid = "uuid";
        long expectedExpirationMillis = System.currentTimeMillis() + (minutesTillExpiration * 60000);

        String token = jwtService.generateToken(username, role, uuid);

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSigningKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long actualExpirationMillis = claims.getExpiration().getTime();
        assertThat(actualExpirationMillis)
                .isBetween(expectedExpirationMillis - 5000, expectedExpirationMillis + 5000); // margen de 5s
    }

    @Test
    void validateToken_WithValidToken_DoesNotThrow() {
        String token = jwtService.generateToken("testUser", "USER", "uuid-1234");
        jwtService.validateToken(token); // No debe lanzar excepción
    }

    @Test
    void validateToken_WithInvalidToken_ShouldThrowJwtException() {
        String invalidToken = "this.is.an.invalid.token";
        assertThatThrownBy(() -> jwtService.validateToken(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }

    @Test
    void validateToken_WithExpiredToken_ShouldThrowExpiredJwtException() {
        JwtService shortLivedService = new JwtService(jwtSigningKey, 0L); // 0 min duración
        String token = shortLivedService.generateToken("expiredUser", "USER", "uuid");
        try {
            Thread.sleep(1000); // Esperar 1 segundo para garantizar que expire
        } catch (InterruptedException ignored) {}

        assertThatThrownBy(() -> shortLivedService.validateToken(token))
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("Token expired but logout successful");
    }

    @Test
    void extractBearerToken_WithValidHeader_ReturnsToken() {
        String token = "abc.def.ghi";
        String header = "Bearer " + token;

        String result = jwtService.extractBearerToken(header);

        assertThat(result).isEqualTo(token);
    }

    @Test
    void extractBearerToken_WithNullHeader_ThrowsJwtException() {
        assertThatThrownBy(() -> jwtService.extractBearerToken(null))
                .isInstanceOf(JwtException.class)
                .hasMessage("Authorization header is missing or malformed");
    }

    @Test
    void extractBearerToken_WithMalformedHeader_ThrowsJwtException() {
        String malformedHeader = "Token abc.def.ghi";

        assertThatThrownBy(() -> jwtService.extractBearerToken(malformedHeader))
                .isInstanceOf(JwtException.class)
                .hasMessage("Authorization header is missing or malformed");
    }

    @Test
    void extractAllClaims_WithValidToken_ShouldReturnClaims() {
        String token = jwtService.generateToken("testUser", "USER", "uuid-123");
        Claims claims = jwtService.extractAllClaims(token);

        assertThat(claims.getSubject()).isEqualTo("testUser");
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.get("uuid", String.class)).isEqualTo("uuid-123");
    }

    @Test
    void extractAllClaims_WithExpiredToken_ShouldThrowExpiredJwtException() {
        JwtService shortLivedJwtService = new JwtService(jwtSigningKey, 0L);
        String token = shortLivedJwtService.generateToken("expiredUser", "USER", "uuid-456");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}

        assertThatThrownBy(() -> shortLivedJwtService.extractAllClaims(token))
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("Token expired");
    }

    @Test
    void extractAllClaims_WithInvalidToken_ShouldThrowJwtException() {
        String invalidToken = "this.is.not.valid";

        assertThatThrownBy(() -> jwtService.extractAllClaims(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }

    @Test
    void switchRole_WithValidChange_ShouldReturnNewToken() {
        String originalToken = jwtService.generateToken("testUser", "USER", "uuid-001");

        String switchedToken = jwtService.switchRole(originalToken, "ADMIN");

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSigningKey)))
                .build()
                .parseSignedClaims(switchedToken)
                .getPayload();

        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.get("isTemporaryRole", Boolean.class)).isTrue();
        assertThat(claims.get("uuid", String.class)).isEqualTo("uuid-001");
        assertThat(claims.getSubject()).isEqualTo("testUser");
    }

    @Test
    void switchRole_SameRole_ShouldThrowException() {
        String token = jwtService.generateToken("testUser", "USER", "uuid-002");

        assertThatThrownBy(() -> jwtService.switchRole(token, "USER"))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessage("New role is the same as current role.");
    }

    @Test
    void switchRole_InvalidRequestedRole_ShouldThrowException() {
        String token = jwtService.generateToken("testUser", "USER", "uuid-003");

        assertThatThrownBy(() -> jwtService.switchRole(token, "GUEST"))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessage("Requested role change is not allowed.");
    }

    @Test
    void switchRole_InvalidToken_ShouldThrowJwtException() {
        String invalidToken = "not.a.valid.token";

        assertThatThrownBy(() -> jwtService.switchRole(invalidToken, "ADMIN"))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }
}
