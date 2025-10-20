package com.itachallenge.user.exception;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.dto.APIErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    void testHandleUnmodifiableSolutionException() {
        UnmodificableSolutionException exception = new UnmodificableSolutionException("There's an existing solution with status 'ENDED'.");

        request = MockServerHttpRequest.get("/test-path").build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleUnmodifiableSolutionException(exception, exchange);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("There's an existing solution with status 'ENDED'.", response.getBody().getMessage());
        assertEquals("There's an existing solution with status 'ENDED'.", response.getBody().getError());
        assertEquals("/test-path", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }


    @Test
    void testHandleUsernameAlreadyExistsException() {
        UsernameAlreadyExistsException exception = new UsernameAlreadyExistsException("testuser");

        request = MockServerHttpRequest.get("/test-path").build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleUsernameAlreadyExistsException(exception, exchange);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("The username already exists", response.getBody().getMessage());
        assertEquals("The username already exists", response.getBody().getError());
        assertEquals("/test-path", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        BindingResult bindingResult = mock(BindingResult.class);
        org.springframework.core.MethodParameter methodParameter = mock(org.springframework.core.MethodParameter.class);

        java.lang.reflect.Method realMethod;
        try {
            realMethod = UserGlobalExceptionHandlerTest.class.getDeclaredMethod("testHandleMethodArgumentNotValidException");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        when(methodParameter.getExecutable()).thenReturn(realMethod);
        when(methodParameter.getParameterIndex()).thenReturn(0);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        request = MockServerHttpRequest.get("/test-path").build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleMethodArgumentNotValidException(exception, exchange);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), response.getBody().getError());
        assertEquals("/test-path", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
        assertNotNull(response.getBody().getMessage());
    }

    @Test
    void testHandleInternalServerErrorException() {
        InternalServerErrorException exception = new InternalServerErrorException("Unexpected internal error");

        request = MockServerHttpRequest.get("/test-path").build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleInternalServerErrorException(exception, exchange);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getBody().getError());
        assertEquals("Unexpected internal error", response.getBody().getMessage());
        assertEquals("/test-path", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }


    @Test
    void handleGithubUnavailable_shouldReturn503() {
        String expectedPath = "/itachallenge/api/v1/user/users/testbUser";
        Throwable connectCause = new java.net.ConnectException("Connection refused");
        GithubUnavailableException ex = new GithubUnavailableException("Service error ocurred.", connectCause);

        request = MockServerHttpRequest.get(expectedPath).build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex, exchange);

        APIErrorResponse responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), responseBody.getStatus()); // 503
        assertEquals(UserGlobalExceptionHandler.GITHUB_ERROR_SUMMARY, responseBody.getError());
        assertEquals("The external GitHub service is currently unavailable.", responseBody.getMessage());
        assertEquals(expectedPath, responseBody.getPath());
        assertNotNull(responseBody.getTimestamp());
    }

    @Test
    void handleGithubUnavailable_shouldReturn504() {
        String expectedPath = "/itachallenge/api/v1/user/users/testUser";
        Throwable timeoutCause = new java.net.SocketTimeoutException("Read timed out");
        GithubUnavailableException ex = new GithubUnavailableException("Service error ocurred.", timeoutCause);

        request = MockServerHttpRequest.get(expectedPath).build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex, exchange);

        APIErrorResponse responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
        assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), responseBody.getStatus());
        assertEquals(UserGlobalExceptionHandler.GITHUB_ERROR_SUMMARY, responseBody.getError());
        assertEquals("The external GitHub service timed out.", responseBody.getMessage());
        assertEquals(expectedPath, responseBody.getPath());
        assertNotNull(responseBody.getTimestamp());
    }

    @Test
    void handleGithubUnavailable_shouldReturn503_onOtherCauses() {
        String expectedPath = "/itachallenge/api/v1/user/users/anyGithubUser";
        GithubUnavailableException ex = new GithubUnavailableException("Unknown error.");

        request = MockServerHttpRequest.get(expectedPath).build();
        exchange = MockServerWebExchange.from(request);

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex, exchange);

        APIErrorResponse responseBody = Objects.requireNonNull(response.getBody());

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), responseBody.getStatus());
        assertEquals(UserGlobalExceptionHandler.GITHUB_ERROR_SUMMARY, responseBody.getError());
        assertEquals("An external service error occurred.", responseBody.getMessage());
        assertEquals(expectedPath, responseBody.getPath());
        assertNotNull(responseBody.getTimestamp());
    }

}
