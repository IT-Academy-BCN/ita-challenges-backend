package com.itachallenge.user.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.dto.APIErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
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
        ResponseEntity<String> response = exceptionHandler.handleBadRequestException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request error", response.getBody());
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
        ResponseEntity<String> response = exceptionHandler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
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


    @Test
    void handleGithubUnavailable_shouldReturn503() {
        Throwable connectCause = new ConnectException("Connection refused");
        GithubUnavailableException ex = new GithubUnavailableException("Service error ocurred.", connectCause);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(),
                "The handler must return HTTP 503 for a ConnectException cause.");
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("unavailable"),
                "The secured message should indicate service unavailability.");
    }

    @Test
    void handleGithubUnavailable_shouldReturn504() {
        Throwable timeoutCause = new SocketTimeoutException("Read timed out");
        GithubUnavailableException ex = new GithubUnavailableException("Service error ocurred.", timeoutCause);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode(),
        "The handler must return HTTP 504 for a SocketTimeoutException cause.");
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("timed out"),
                "The secured message should indicate a timeout.");
    }

    @Test
    void handleGithubUnavailable_shouldReturn503ForOtherCauses() {
        GithubUnavailableException ex = new GithubUnavailableException("Unknown error.");

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(),
                "The handler must return HTTP 503 for other types of causes (not recognized or null).");
        assertTrue(Objects.requireNonNull(response.getBody()).getMessage().contains("external service error"),
                "The secured message should indicate an external service error.");
    }

}

