package com.itachallenge.user.exception;

import com.itachallenge.user.dtos.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;


    @Test
    void testHandleConstraintViolation() {
        var violation1 = mock(ConstraintViolation.class);
        when(violation1.getMessage()).thenReturn("failed 1");

        Set<ConstraintViolation<?>> violations = new HashSet<>();
        violations.add(violation1);

        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleConstraintViolation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("failed 1", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void testHandleIllegalArgumentException() {

        IllegalArgumentException ex = new IllegalArgumentException("Test error message");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error message", response.getBody().getMessage());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = createMethodArgumentNotValidException("objectName", "fieldName", "Test error message");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleMethodArgumentNotValidException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test error message", response.getBody().getMessage());
    }

    private MethodArgumentNotValidException createMethodArgumentNotValidException(String objectName, String fieldName, String errorMessage) {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError(objectName, fieldName, errorMessage);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);
        when(bindingResult.getTarget()).thenReturn(methodParameter);

        return new MethodArgumentNotValidException(methodParameter, bindingResult);
    }
}
