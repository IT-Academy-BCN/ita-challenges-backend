package com.itachallenge.auth.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleExpiredJwtException() {
        Header header = mock(Header.class);
        Claims claims = mock(Claims.class);
        String message = "Token expired during logout validation";
        ExpiredJwtException ex = new ExpiredJwtException(header, claims, message);

        ResponseEntity<Map<String, String>> response = handler.handleExpiredJwt(ex);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().get("message"));
    }

    @Test
    void testHandleJwtException() {
        String message = "Token is invalid or tampered";
        JwtException ex = new JwtException(message);

        ResponseEntity<Map<String, String>> response = handler.handleJwtException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().get("message"));
    }

    @Test
    void handleInvalidRoleChange_ShouldReturnBadRequestWithMessage() {
        String message = "Invalid role.";
        InvalidRoleChangeRequestException exception = new InvalidRoleChangeRequestException(message);

        ResponseEntity<Map<String, String>> response = handler.handleInvalidRoleChange(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().get("message"));
    }
}
