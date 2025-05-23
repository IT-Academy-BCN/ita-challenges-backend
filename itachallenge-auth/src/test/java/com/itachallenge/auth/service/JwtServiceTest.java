package com.itachallenge.auth.service;

import com.itachallenge.auth.exception.InvalidRoleChangeRequestException;
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
    void switchRole_ShouldReturnTokenWithUpdatedRole() {
        String token = jwtService.generateToken("user1", "ADMIN", "uuid-1");
        String newToken = jwtService.switchRole(token, "USER");

        Claims claims = jwtService.extractAllClaims(newToken);
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.get("isTemporaryRole", Boolean.class)).isTrue();
    }

    @Test
    void switchRole_WithSameRole_ShouldThrowException() {
        String token = jwtService.generateToken("user1", "USER", "uuid-1");

        assertThatThrownBy(() -> jwtService.switchRole(token, "USER"))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessage("New role is the same as current role.");
    }

    @Test
    void switchRole_WithInvalidCurrentRole_ShouldThrowException() {
        String invalidRoleToken = Jwts.builder()
                .subject("user2")
                .claim("role", "GUEST")
                .claim("uuid", "uuid-2")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSigningKey)))
                .compact();

        assertThatThrownBy(() -> jwtService.switchRole(invalidRoleToken, "USER"))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessage("Requested role change is not allowed.");
    }

    @Test
    void switchRole_WithNullRequestedRole_ShouldThrowException() {
        String token = jwtService.generateToken("user3", "ADMIN", "uuid-3");

        assertThatThrownBy(() -> jwtService.switchRole(token, null))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessage("New role must be provided.");
    }
}
