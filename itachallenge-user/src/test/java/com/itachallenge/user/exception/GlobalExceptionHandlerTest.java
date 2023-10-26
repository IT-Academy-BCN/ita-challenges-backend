package com.itachallenge.user.exception;

import com.itachallenge.user.dtos.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

}
