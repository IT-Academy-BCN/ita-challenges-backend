package com.itachallenge.user.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.itachallenge.user.controller.UserController;
import com.itachallenge.user.dto.APIErrorResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebExchange;

import com.itachallenge.user.dto.FieldErrorDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class UserGlobalExceptionHandlerTest {

    private UserGlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MessageSource messageSource = mock(MessageSource.class);
        exceptionHandler = new UserGlobalExceptionHandler(messageSource);
    }

    private ServerWebExchange mockExchange() {
        ServerWebExchange exchange = Mockito.mock(ServerWebExchange.class);
        ServerHttpRequest request = Mockito.mock(ServerHttpRequest.class);
        RequestPath requestPath = Mockito.mock(RequestPath.class);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/api/v1/user");

        return exchange;
    }

    @Test
    @DisplayName("should return 500 Internal Server Error response when an unexpected exception occurs")
    void testHandleAny() {
        Exception exception = new Exception("Unexpected Error");
        ServerWebExchange exchange = mockExchange();

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleAny(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(body);

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus()),
                () -> assertEquals("Internal Server Error", body.getError()),
                () -> assertEquals("An unexpected error occurred.", body.getMessage()),
                () -> assertEquals("/api/v1/user", body.getPath()),
                () -> assertTrue(body.getTimestamp().isBefore(Instant.now().plusSeconds(1)))
                );
    }

    @Test
    @DisplayName("should return 400 Bad Request response when an IllegalArgumentException is thrown")
    void testHandleIllegalArgument() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ServerWebExchange exchange = mockExchange();

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getError()),
                () -> assertEquals("Invalid input provided. Please check your request.", body.getMessage()),
                () -> assertEquals("/api/v1/user", body.getPath()),
                () -> assertTrue(body.getTimestamp().isBefore(Instant.now().plusSeconds(1)))
        );
    }

@Test
@DisplayName("should return 400 Bad Request with detailed field errors when validation fails")
void handleValidationExceptions_shouldReturnBadRequestWithFieldErrors() {
    ServerWebExchange exchange = mockExchange();

    ConstraintViolation<?> violation1 = createMockViolation(
            "email",
            "must be a valid email",
            TestEntity.class
    );

    ConstraintViolation<?> violation2 = createMockViolation(
            "name",
            "must not be blank",
            TestEntity.class
    );

    Set<ConstraintViolation<?>> violations = Set.of(violation1, violation2);
    ConstraintViolationException exception = new ConstraintViolationException(violations);

    ResponseEntity<APIErrorResponse> response = exceptionHandler.handleValidationExceptions(exception, exchange);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    APIErrorResponse body = response.getBody();

    assertNotNull(body);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    assertAll("APIErrorResponse validation",
            () -> assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus()),
            () -> assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getError()),
            () -> assertEquals("Validation failed.", body.getMessage()),
            () -> assertEquals("/api/v1/user", body.getPath()),
            () -> assertTrue(body.getTimestamp().isBefore(Instant.now().plusSeconds(1)))
    );

    List<FieldErrorDto> errors = body.getErrors();
    assertNotNull(errors);
    assertEquals(2, errors.size());

    assertAll("Field errors validation",
            () -> {
                Set<String> fields = errors.stream()
                        .map(FieldErrorDto::getField)
                        .collect(Collectors.toSet());
                assertEquals(Set.of("email", "name"), fields);
            },

            () -> {
                Set<String> messages = errors.stream()
                        .map(FieldErrorDto::getMessage)
                        .collect(Collectors.toSet());
                assertEquals(Set.of("must be a valid email", "must not be blank"), messages);
            },

            () -> assertTrue(errors.stream()
                            .allMatch(error -> "TestEntity".equals(error.getObjectName())))
    );
}

    @Test
    @DisplayName("Should return bad request when method argument type mismatch occurs")
    void testHandleTypeMismatchException() {
        MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getContainingClass()).thenReturn((Class) UserController.class);

        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("Solution");
        when(exception.getValue()).thenReturn(123);
        when(exception.getRequiredType()).thenReturn((Class) String.class);
        when(exception.getParameter()).thenReturn(methodParameter);

        ServerWebExchange exchange = mockExchange();

        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleTypeMismatchException(exception, exchange);
        APIErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertAll("APIErrorResponse validation",
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), body.getError()),
                () -> assertEquals("MethodArgumentTypeMismatchException", body.getMessage()),
                () -> assertEquals("/api/v1/user", body.getPath()),
                () -> assertTrue(body.getTimestamp().isBefore(Instant.now().plusSeconds(1)))
        );

        List<FieldErrorDto> errors = body.getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());

        FieldErrorDto errorDto = errors.get(0);
        assertAll("FieldErrorDto validation",
                () -> assertEquals("Solution", errorDto.getField()),
                () -> assertEquals("UserController", errorDto.getObjectName()),
                () -> assertTrue(errorDto.getMessage().contains("'123'")),
                () -> assertTrue(errorDto.getMessage().contains("String"))
        );
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


//    @Test
//    void handleGithubUnavailable_shouldReturn503() {
//        GithubUnavailableException ex = new GithubUnavailableException("Some 5xx error");
//
//        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);
//
//        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
//        assertEquals("GitHub API error", response.getBody().getError());
//    }
//
//    @Test
//    void handleGithubUnavailable_shouldReturn504() {
//        GithubUnavailableException ex = new GithubUnavailableException("timeout");
//
//        ResponseEntity<APIErrorResponse> response = exceptionHandler.handleGithubUnavailable(ex);
//
//        assertEquals(HttpStatus.GATEWAY_TIMEOUT, response.getStatusCode());
//        assertEquals("GitHub API error", response.getBody().getError());
//    }

    private ConstraintViolation<?> createMockViolation(String propertyPath, String message, Class<?> rootBeanClass) {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn(message);
        when(violation.getRootBeanClass()).thenReturn((Class) rootBeanClass);

        return violation;
    }

    // Test entity class for mocking
    static class TestEntity {
    }
}

