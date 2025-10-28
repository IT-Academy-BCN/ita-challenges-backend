package com.itachallenge.errorcore.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseExceptionHandlerUnitTest {

    @Mock
    private ErrorResponseBuilder builder;

    @Mock
    private HttpServletRequest request;

    private TestExceptionHandler handler;

    private final APIErrorResponse dummyResponse = APIErrorResponse.builder()
            .status(400)
            .error("Bad Request")
            .message("dummy")
            .build();

    static class TestExceptionHandler extends BaseExceptionHandler {
        public TestExceptionHandler(ErrorResponseBuilder responseBuilder) {
            super(responseBuilder);
        }
    }

    @BeforeEach
    void setUp() {
        handler = new TestExceptionHandler(builder);
    }

    @Test
    void handleAny_shouldReturnInternalServerError() {
        when(builder.buildError(eq(HttpStatus.INTERNAL_SERVER_ERROR), anyString(), eq(request)))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleAny(new Exception("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(builder).buildError(eq(HttpStatus.INTERNAL_SERVER_ERROR), contains("boom"), eq(request));
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        when(builder.buildError(eq(HttpStatus.BAD_REQUEST), anyString(), eq(request)))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("invalid input"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(eq(HttpStatus.BAD_REQUEST), contains("invalid input"), eq(request));
    }

    @Test
    void handleValidationExceptions_shouldDelegateToBuilder() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        when(builder.buildConstraintViolationErrorResponse(ex, request)).thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleValidationExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildConstraintViolationErrorResponse(ex, request);
    }

    @Test
    void handleTypeMismatchException_shouldDelegateToBuilder() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(builder.buildTypeMismatchErrorResponse(ex, request)).thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleTypeMismatchException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildTypeMismatchErrorResponse(ex, request);
    }

    @Test
    void handleMethodArgumentNotValidException_shouldDelegateToBuilder() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(builder.buildArgumentNotValidErrorResponse(ex, request)).thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildArgumentNotValidErrorResponse(ex, request);
    }

    @Test
    void handleResponseStatusException_shouldUseStatusFromException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        when(builder.buildStatusErrorResponse(ex, request)).thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleResponseStatusException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(builder).buildStatusErrorResponse(ex, request);
    }

    @Test
    void handleInvalidFormat_shouldReturnBadRequestAndDelegateToBuilder() {
        InvalidFormatException ex = mock(InvalidFormatException.class);
        when(builder.resolveMessage(any())).thenReturn("Invalid or malformed request.");
        when(builder.buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleInvalidFormat(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(dummyResponse);
        verify(builder).buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request);
    }

    @Test
    void handleWebFluxBindingErrors_withInvalidFormatCause_shouldDelegateToHandleInvalidFormat() {
        InvalidFormatException rootCause = mock(InvalidFormatException.class);
        when(builder.resolveMessage(any())).thenReturn("Invalid or malformed request.");
        HttpInputMessage mockInput = new MockHttpInputMessage("{}".getBytes());
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", rootCause, mockInput);
        when(builder.buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request);
    }

    @Test
    void handleWebFluxBindingErrors_withoutInvalidFormatCause_shouldReturnGenericBadRequest() {
        ServerWebInputException ex = new ServerWebInputException("Bad JSON");
        when(builder.buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request))
                .thenReturn(dummyResponse);
        when(builder.resolveMessage(any())).thenReturn("Invalid or malformed request.");
        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(HttpStatus.BAD_REQUEST, "Invalid or malformed request.", request);
    }
}
