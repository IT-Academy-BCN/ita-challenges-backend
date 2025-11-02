package com.itachallenge.user.exception;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.GlobalExceptionHandler;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class UserExceptionHandlerTest {

    @Mock
    private ErrorResponseBuilder responseBuilder;

    @Mock
    private HttpServletRequest request;

    private final UserExceptionHandler userExceptionHandler = new UserExceptionHandler();

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;


    private APIErrorResponse fakeResponse(HttpStatus status, String message) {
        return APIErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path("/fake-path")
                .errors(List.of())
                .build();
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- BaseExceptionHandler inherited tests ----------------

    @Test
    void testHandleAny() {
        Exception exception = new Exception("Unexpected Error");
        when(responseBuilder.buildError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleAny(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected Error", response.getBody().getMessage());
        verify(responseBuilder).buildError(exception, request);
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        when(responseBuilder.buildError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.BAD_REQUEST, exception.getMessage()));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleIllegalArgument(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationExceptions() {
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", null);
        when(responseBuilder.buildConstraintViolationErrorResponse(exception, request))
                .thenReturn(fakeResponse(HttpStatus.BAD_REQUEST, "Validation failed"));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
    }

    @Test
    void testHandleTypeMismatchException() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(responseBuilder.buildTypeMismatchErrorResponse(exception, request))
                .thenReturn(fakeResponse(HttpStatus.BAD_REQUEST, "Invalid parameter format."));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleTypeMismatchException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter format.", response.getBody().getMessage());
    }

    // ---------------- UserExceptionHandler custom tests ----------------

    @Test
    void testHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("Bad request error");
        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.BAD_REQUEST, exception.getMessage()));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request error", response.getBody().getMessage());
    }

    @Test
    void testHandleBadUUIDException() {
        BadUUIDException exception = new BadUUIDException("invalid uuid");
        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.BAD_REQUEST, "validation.uuid.invalid"));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("validation.uuid.invalid", response.getBody().getMessage());
    }

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Resource not found");
        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.NOT_FOUND, exception.getMessage()));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void testHandleUnmodifiableSolutionException() {
        String message = "There's an existing solution with status 'SUBMITTED_COMPLETE'.";
        UnmodificableSolutionException exception = new UnmodificableSolutionException(message);
        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.CONFLICT, message));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void testHandleInternalServerErrorException() {
        InternalServerErrorException exception = new InternalServerErrorException("Server exploded");
        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage()));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server exploded", response.getBody().getMessage());
    }

    @Test
    void testHandleUsernameAlreadyExistsException() {
        String username = "alfonso79";
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException(username);
        String expectedMessage = "The username '" + username + "' is already registered.";

        when(responseBuilder.buildCustomExceptionError(exception, request))
                .thenReturn(fakeResponse(HttpStatus.CONFLICT, expectedMessage));

        ResponseEntity<APIErrorResponse> response = globalExceptionHandler.handleApiCustomException(exception, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody().getMessage());
    }

    // ---------------- GithubUnavailableException handler ----------------

    @Test
    void handleGithubUnavailable_shouldReturn503() {
        Throwable connectCause = new ConnectException("Connection refused");
        GithubUnavailableException ex = new GithubUnavailableException("Service error occurred.", connectCause);

        ResponseEntity<APIErrorResponse> response = userExceptionHandler.handleGithubUnavailable(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(),
                "The handler must return HTTP 503 for a ConnectException cause.");
        assertTrue(response.getBody().getMessage().contains("unavailable"));
    }

    @Test
    void handleGithubUnavailable_shouldReturn504() {
        Throwable timeoutCause = new SocketTimeoutException("Read timed out");
        GithubUnavailableException ex = new GithubUnavailableException("Service error occurred.", timeoutCause);
        APIErrorResponse fake = fakeResponse(HttpStatus.GATEWAY_TIMEOUT, "The external GitHub service timed out.");

        ResponseEntity<APIErrorResponse> response = userExceptionHandler.handleGithubUnavailable(ex, request);

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode(),
                "The handler must return HTTP 504 for a SocketTimeoutException cause.");
        assertTrue(response.getBody().getMessage().contains("timed out"));
    }

    @Test
    void handleGithubUnavailable_shouldReturn503ForOtherCauses() {
        GithubUnavailableException ex = new GithubUnavailableException("Unknown error.");
        APIErrorResponse fake = fakeResponse(HttpStatus.SERVICE_UNAVAILABLE, "An external service error occurred.");

        ResponseEntity<APIErrorResponse> response = userExceptionHandler.handleGithubUnavailable(ex, request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode(),
                "The handler must return HTTP 503 for other causes.");
        assertTrue(response.getBody().getMessage().contains("external service error"));
    }
}
