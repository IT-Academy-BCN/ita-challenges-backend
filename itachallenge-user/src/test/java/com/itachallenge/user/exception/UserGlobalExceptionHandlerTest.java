package com.itachallenge.user.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.dto.APIErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import java.util.Objects;

class UserGlobalExceptionHandlerTest {

    private UserGlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new UserGlobalExceptionHandler();
    }

    @Test
    void testHandleAny() {
        Exception exception = new Exception("Unexpected Error");

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        Mockito.when(requestPath.value()).thenReturn("/api/v1/user/123");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleAny(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals("Internal Server Error", body.getError());
        assertEquals("An unexpected error occurred. Please try again later.", body.getMessage());
        assertEquals("/api/v1/user/123", body.getPath());

    }


    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        Mockito.when(requestPath.value()).thenReturn("/api/v1/user/123");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Illegal argument", body.getError());
        assertEquals("Invalid input provided. Please check your request.", body.getMessage());
        assertEquals("/api/v1/user/123", body.getPath());
    }

    @Test
    void testHandleValidationExceptions() {
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", null);

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        Mockito.when(requestPath.value()).thenReturn("/api/v1/user/123");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleValidationExceptions(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Validation failed", body.getError());
        assertEquals("Validation failed for one or more fields. Please check your request.", body.getMessage());
        assertEquals("/api/v1/user/123", body.getPath());
    }

    @Test
    void testHandleTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        ResponseEntity<String> response = exceptionHandler.handleTypeMismatchException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter format.", response.getBody());
    }

    @Test
    void testHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request error");

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        Mockito.when(requestPath.value()).thenReturn("/api/v1/user/123");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleBadRequestException(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Bad request error", body.getMessage());
        assertEquals("/api/v1/user/123", body.getPath());
    }

    @Test
    void testHandleDatabaseException() {
        DatabaseException exception = new DatabaseException("Database connection failed");
        ResponseEntity<String> response = exceptionHandler.handleDatabaseException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Database error: Database connection failed", response.getBody());
    }

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");

        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        Mockito.when(exchange.getRequest()).thenReturn(request);
        Mockito.when(request.getPath()).thenReturn(requestPath);
        Mockito.when(requestPath.value()).thenReturn("/api/v1/user/123");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleNotFoundException(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body);
        assertNotNull(body.getTimestamp());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("Not found", body.getError());
        assertEquals("Resource not found", body.getMessage());
        assertEquals("/api/v1/user/123", body.getPath());    }

    @Test
    void testHandleUnmodifiableSolutionException(){
        String message = "There's an existing solution with status 'ENDED'.";
        UnmodificableSolutionException exception = new UnmodificableSolutionException(message);
        ResponseEntity<String> response = exceptionHandler.handleUnmodifiableSolutionException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

    @Test
    void testHandleUsernameAlreadyExistsException() {
        String username = "alfonso79";
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(username);
        ResponseEntity<String> response = exceptionHandler.handleUsernameAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The username 'alfonso79' is already registered.", response.getBody());
    }


    @Test
    void handleGithubUnavailable_shouldReturn503() {
        GithubUnavailableException ex = new GithubUnavailableException("Some 5xx error");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("GitHub API error", response.getBody().getError());
    }

    @Test
    void handleGithubUnavailable_shouldReturn504() {
        GithubUnavailableException ex = new GithubUnavailableException("timeout");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertEquals("GitHub API error", response.getBody().getError());
    }

}

