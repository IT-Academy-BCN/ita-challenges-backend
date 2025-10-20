package com.itachallenge.user.exception;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.itachallenge.user.dto.APIErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

class UserGlobalExceptionHandlerTest {

    private UserGlobalExceptionHandler exceptionHandler;
    private ServerWebExchange exchange;
    private MockServerHttpRequest request;

    @BeforeEach
    void setUp() {
        MessageSource messageSource = mock(MessageSource.class);
        exceptionHandler = new UserGlobalExceptionHandler(messageSource);
    }

    @Test
    void testHandleAny() {
        Exception exception = new Exception("Unexpected Error");
        ResponseEntity<String> response = exceptionHandler.handleAny(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Unexpected error happened."));
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ResponseEntity<String> response = exceptionHandler.handleIllegalArgument(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody());
    }

    @Test
    void testHandleValidationExceptions() {
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", null);
        ResponseEntity<String> response = exceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody());
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

        request = MockServerHttpRequest.get("/test-path").build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleBadRequestException(exception, exchange);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request error", response.getBody().getMessage());
        assertEquals("Bad Request", response.getBody().getError());
        assertEquals("/test-path", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleBadUUIDException() {
        BadUUIDException exception = new BadUUIDException("Invalid UUID format");

        request = mock(MockServerHttpRequest.class);
        exchange = mock(MockServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(RequestPath.parse("/test/uuid", ""));

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleBadUUIDException(exception, exchange);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        APIErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("The provided IDs are not valid.", body.getMessage());
        assertEquals("/test/uuid", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void testHandleDatabaseException() {
        DatabaseException exception = new DatabaseException("Database connection failed");

        request = mock(MockServerHttpRequest.class);
        exchange = mock(MockServerWebExchange.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(RequestPath.parse("/test/database", ""));

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleDatabaseException(exception, exchange);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        APIErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
        assertEquals("Database Error", body.getError());
        assertEquals("Database connection failed", body.getMessage());
        assertEquals("/test/database", body.getPath());
        assertNotNull(body.getTimestamp());
    }



    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");

        exchange = mock(MockServerWebExchange.class);
        request = mock(MockServerHttpRequest.class);
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(RequestPath.parse("/test/resource", ""));

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleNotFoundException(exception, exchange);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        APIErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Resource not found", body.getMessage());
        assertEquals("/test/resource", body.getPath());
        assertNotNull(body.getTimestamp());
    }


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
}

