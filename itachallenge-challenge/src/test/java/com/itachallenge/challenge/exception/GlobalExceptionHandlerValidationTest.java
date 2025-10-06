package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.APIErrorResponse;
import com.itachallenge.challenge.dto.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Validation in GlobalExceptionHandler
 * Focused on verifying correct messageSource usage and APIErrorResponse structure.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerValidationTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException ex;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setup() {
        when(request.getRequestURI()).thenReturn("/api/test");
        when(messageSource.getMessage(any(FieldError.class), any(Locale.class)))
                .thenReturn("mocked validation message");
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequestWithStructuredBody() {
        // Arrange
        FieldError fieldError = new FieldError("TestDto", "fieldName", "default message");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(bindingResult.getObjectName()).thenReturn("TestDto");
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<APIErrorResponse> response =
                globalExceptionHandler.handleMethodArgumentNotValidException(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        APIErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("/api/test", body.getPath());
        assertTrue(body.getMessage().contains("TestDto"),
                "Message should contain 'TestDto'");

        // Check that field errors are correctly mapped
        assertNotNull(body.getErrors());
        assertEquals(1, body.getErrors().size());

        FieldErrorDto errorDto = body.getErrors().get(0);
        assertEquals("TestDto", errorDto.getObjectName());
        assertEquals("fieldName", errorDto.getField());
        assertEquals("mocked validation message", errorDto.getMessage());

        // Verify messageSource was called properly
        verify(messageSource, times(1))
                .getMessage(any(FieldError.class), any(Locale.class));
    }

    @Test
    void handleMethodArgumentNotValid_ShouldHandleMultipleFieldErrors() {
        // Arrange
        FieldError f1 = new FieldError("Object", "first", "error1");
        FieldError f2 = new FieldError("Object", "second", "error2");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(f1, f2));
        when(bindingResult.getObjectName()).thenReturn("Object");
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<APIErrorResponse> response =
                globalExceptionHandler.handleMethodArgumentNotValidException(ex, request);

        // Assert
        APIErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.getErrors().size());

        verify(messageSource, times(2))
                .getMessage(any(FieldError.class), any(Locale.class));
    }
}

