package com.itachallenge.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

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
    void switchRole_ShouldChangeRoleFromAdminToUser() {
        String username = "testUser";
        String uuid = "uuid-1234";
        String originalToken = jwtService.generateToken(username, "ADMIN", uuid);

        String switchedToken = jwtService.switchRole(originalToken);
        Claims claims = jwtService.extractAllClaims(switchedToken);

        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.get("uuid", String.class)).isEqualTo(uuid);

        Claims originalClaims = jwtService.extractAllClaims(originalToken);
        assertThat(claims.getIssuedAt()).isEqualTo(originalClaims.getIssuedAt());
        assertThat(claims.getExpiration()).isEqualTo(originalClaims.getExpiration());
    }

    @Test
    void switchRole_ShouldChangeRoleFromUserToAdmin() {
        String username = "anotherUser";
        String uuid = "uuid-5678";
        String originalToken = jwtService.generateToken(username, "USER", uuid);

        String switchedToken = jwtService.switchRole(originalToken);
        Claims claims = jwtService.extractAllClaims(switchedToken);

        assertThat(claims.get("role", String.class)).isEqualTo("ADMIN");
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("uuid", String.class)).isEqualTo(uuid);
    }

    @Test
    void switchRole_WithInvalidToken_ShouldThrowJwtException() {
        String invalidToken = "invalid.token.value";

        assertThatThrownBy(() -> jwtService.switchRole(invalidToken))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }

    @Test
    void switchRole_WithExpiredToken_ShouldThrowResponseStatusExceptionWith200() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(jwtSigningKey, 0L);
        String expiredToken = shortLivedJwtService.generateToken("expiredUser", "USER", "uuid");
        Thread.sleep(1000); // asegurar que expire

        assertThatThrownBy(() -> shortLivedJwtService.switchRole(expiredToken))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> {
                    ResponseStatusException ex = (ResponseStatusException) e;
                    assertThat(ex.getStatusCode().value()).isEqualTo(200);
                    assertThat(ex.getReason()).isEqualTo("Token is expired.");
                });
    }

}
