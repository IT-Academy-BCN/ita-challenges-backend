package com.itachallenge.challenge.service;

import com.itachallenge.jwtcore.service.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChallengeJwtFacadeTest {

    private final IJwtService jwtService = mock(IJwtService.class);
    private final ChallengeJwtFacade facade = new ChallengeJwtFacade(jwtService);

    @Test
    void getUsernameFromAuthenticationHeader_whenHeaderIsNull_returnsNull() {
        assertNull(facade.getUsernameFromAuthenticationHeader(null));
        verifyNoInteractions(jwtService);
    }

    @Test
    void getUsernameFromAuthenticationHeader_whenHeaderIsBlank_returnsNull() {
        assertNull(facade.getUsernameFromAuthenticationHeader("   "));
        verifyNoInteractions(jwtService);
    }

    @Test
    void getUsernameFromAuthenticationHeader_whenTokenInvalid_throwsBadRequestException() {
        String authHeader = "Bearer bad";
        when(jwtService.extractBearerToken(authHeader)).thenThrow(new JwtException("bad token"));
 
        org.junit.jupiter.api.Assertions.assertThrows(
                com.itachallenge.common.exception.BadRequestException.class,
                () -> facade.getUsernameFromAuthenticationHeader(authHeader)
        );

        verify(jwtService).extractBearerToken(authHeader);
        verify(jwtService, never()).extractAllClaims(anyString());
    }

    @Test
    void getUsernameFromAuthenticationHeader_whenTokenValid_returnsSubject() {
        String authHeader = "Bearer ok";
        String token = "token";
        Claims claims = mock(Claims.class);

        when(jwtService.extractBearerToken(authHeader)).thenReturn(token);
        when(jwtService.extractAllClaims(token)).thenReturn(claims);
        when(claims.getSubject()).thenReturn("test-user");

        assertEquals("test-user", facade.getUsernameFromAuthenticationHeader(authHeader));
    }
}
