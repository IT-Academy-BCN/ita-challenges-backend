package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.MessageDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GlobalExceptionHandlerTest.class)
class GlobalExceptionHandlerTest {
    //VARIABLES
    String REQUEST = "Invalid request";
    private final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private final HttpStatus OK_REQUEST = HttpStatus.OK;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    @MockBean
    private ResponseStatusException responseStatusException;
    @MockBean
    private MessageDto errorMessage;
    @MockBean
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleResponseStatusException() {
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        String expectedErrorMessage = "Validation failed";
        ResponseStatusException ex = new ResponseStatusException(expectedStatus, expectedErrorMessage);

        StepVerifier.create(globalExceptionHandler.handleResponseStatusException(ex))
                .consumeNextWith(responseEntity -> {
                    assertEquals(expectedStatus, responseEntity.getStatusCode());
                    assertEquals(expectedErrorMessage, responseEntity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void testHandleResponseStatusException_NullDetailMessageArguments() {
        HttpStatus expectedStatus = HttpStatus.BAD_REQUEST;
        ResponseStatusException ex = mock(ResponseStatusException.class);
        when(ex.getStatusCode()).thenReturn(expectedStatus);
        when(ex.getDetailMessageArguments()).thenReturn(null);

        StepVerifier.create(globalExceptionHandler.handleResponseStatusException(ex))
                .consumeNextWith(responseEntity -> {
                    assertEquals(expectedStatus, responseEntity.getStatusCode());
                    assertEquals("Validation failed", responseEntity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void TestHandleMethodArgumentNotValidException() {

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("object", "field", "message")));
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed");

        // Act & Assert
        StepVerifier.create(globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException))
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                    assertNotNull(responseEntity.getBody());
                    assertTrue(responseEntity.getBody().getMessage().contains("message"));
                })
                .verifyComplete();
    }

    @Test
    void TestHandleMethodArgumentNotValidException_Return_DefaultMessage() {

        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.emptyList()); // No field errors
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(methodArgumentNotValidException.getMessage()).thenReturn("Validation failed due to input errors");

        // Act & Assert
        StepVerifier.create(globalExceptionHandler.handleMethodArgumentNotValidException(methodArgumentNotValidException))
                .assertNext(responseEntity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                    assertNotNull(responseEntity.getBody());
                    assertEquals("Validation failed due to input errors", responseEntity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleConstraintViolation() {
        Set<ConstraintViolation<?>> constraints = new HashSet<>();
        ConstraintViolation<?> constraint = mock(ConstraintViolation.class);
        when(constraint.getMessage()).thenReturn("Expected message");
        constraints.add(constraint);

        ConstraintViolationException exception = new ConstraintViolationException("Validation failed.", constraints);

        StepVerifier.create(globalExceptionHandler.handleConstraintViolation(exception))
                .consumeNextWith(responseEntity -> {
                    assertEquals(OK_REQUEST, responseEntity.getStatusCode());
                    assertTrue(Objects.requireNonNull(responseEntity.getBody()).getMessage().contains("Expected message"));
                })
                .verifyComplete();
    }


    @Test
    void testHandleChallengeNotFoundException() {
        // El mensaje específico aquí es irrelevante para el comportamiento del manejador de excepciones en esta prueba.
        ChallengeNotFoundException exception = new ChallengeNotFoundException("Este mensaje es ignorado en el manejador.");

        StepVerifier.create(globalExceptionHandler.handleChallengeNotFoundException(exception))
                .consumeNextWith(responseEntity -> {
                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertTrue(responseEntity.getBody().getMessage().contains("Challenge Id not found"));
                })
                .verifyComplete();
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        StepVerifier.create(globalExceptionHandler.handleResourceNotFoundException(exception))
                .consumeNextWith(responseEntity -> {
                    assertEquals(OK_REQUEST, responseEntity.getStatusCode());
                    assertTrue(responseEntity.getBody().getMessage().contains("Resource not found"));
                })
                .verifyComplete();
    }

    @Test
    void test_HandleBadUUIDException() {
        BadUUIDException exception = new BadUUIDException("Invalid Id format");

        StepVerifier.create(globalExceptionHandler.handleBadUUIDException(exception))
                .consumeNextWith(responseEntity -> {
                    assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
                    assertTrue(responseEntity.getBody().getMessage().contains("Invalid Id format"));
                })
                .verifyComplete();
    }

}
