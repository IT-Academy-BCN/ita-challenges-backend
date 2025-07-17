package com.itachallenge.auth.service;

import com.itachallenge.auth.exception.InvalidRoleChangeRequestException;
import com.itachallenge.jwtcore.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtRoleSwitchServiceTest {

    private JwtRoleSwitchService jwtRoleSwitchService;
    private IJwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = mock(IJwtService.class);
        jwtRoleSwitchService = new JwtRoleSwitchService(jwtService);
    }

    @Test
    void switchRole_validRoleChange_returnsNewToken() {
        String oldToken = "old.token";
        String username = "testuser";
        String uuid = "uuid-1234";
        String currentRole = "USER";
        String requestedRole = "ADMIN";
        String newToken = "new.token";

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims(oldToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(claims.get("uuid", String.class)).thenReturn(uuid);
        when(claims.get("role", String.class)).thenReturn(currentRole);
        when(jwtService.generateToken(username, requestedRole.toUpperCase(), uuid)).thenReturn(newToken);

        String result = jwtRoleSwitchService.switchRole(oldToken, requestedRole);

        assertThat(result).isEqualTo(newToken);
        verify(jwtService).extractAllClaims(oldToken);
        verify(jwtService).generateToken(username, requestedRole.toUpperCase(), uuid);
    }

    @Test
    void switchRole_invalidRoleChange_throwsException() {
        String oldToken = "old.token";
        String username = "testuser";
        String uuid = "uuid-1234";
        String currentRole = "USER";
        String requestedRole = "USER";

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims(oldToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(claims.get("uuid", String.class)).thenReturn(uuid);
        when(claims.get("role", String.class)).thenReturn(currentRole);

        assertThatThrownBy(() -> jwtRoleSwitchService.switchRole(oldToken, requestedRole))
                .isInstanceOf(InvalidRoleChangeRequestException.class)
                .hasMessageContaining("same as current role");
    }

    @Test
    void switchRole_InvalidRequestedRole_ShouldThrowException() {
        String token = "valid.token";
        String username = "testUser";
        String uuid = "uuid-003";
        String currentRole = "USER";

        Claims claims = mock(Claims.class);
        when(jwtService.extractAllClaims(token)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(claims.get("uuid", String.class)).thenReturn(uuid);
        when(claims.get("role", String.class)).thenReturn(currentRole);

        assertThatThrownBy(() -> jwtRoleSwitchService.switchRole(token, "GUEST"))
                .isInstanceOf(com.itachallenge.auth.exception.InvalidRoleChangeRequestException.class)
                .hasMessage("Requested role change is not allowed.");

    }

    @Test
    void switchRole_InvalidToken_ShouldThrowJwtException() {
        String invalidToken = "not.a.valid.token";

        when(jwtService.extractAllClaims(invalidToken))
                .thenThrow(new JwtException("Invalid or tampered token"));

        assertThatThrownBy(() -> jwtRoleSwitchService.switchRole(invalidToken, "ADMIN"))
                .isInstanceOf(JwtException.class)
                .hasMessageContaining("Invalid or tampered token");
    }
}
