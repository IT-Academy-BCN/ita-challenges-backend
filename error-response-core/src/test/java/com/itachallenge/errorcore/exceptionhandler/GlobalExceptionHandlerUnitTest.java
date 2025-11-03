package com.itachallenge.errorcore.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerUnitTest {

    @Mock
    private ErrorResponseBuilder builder;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler handler;

    private final APIErrorResponse dummyResponse = APIErrorResponse.builder()
            .status(400)
            .error("Bad Request")
            .message("dummy")
            .build();


    @Test
    void handleAny_shouldReturnInternalServerError() {
        when(builder.buildError(any(Exception.class),eq(request)))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleAny(new Exception("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        verify(builder).buildError(any(Exception.class), eq(request));
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        when(builder.buildError(any(IllegalArgumentException.class), eq(request)))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleIllegalArgument(
                new IllegalArgumentException("invalid input"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(any(IllegalArgumentException.class), eq(request));
    }

    @Test
    void handleValidationExceptions_shouldDelegateToBuilder() {
        ConstraintViolationException ex = new ConstraintViolationException(Set.of());
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
        when(builder.buildError(ex, request))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleInvalidFormat(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(dummyResponse);
        verify(builder).buildError(ex, request);
    }

    @Test
    void handleWebFluxBindingErrors_withInvalidFormatCause_shouldDelegateToHandleInvalidFormat() {
        InvalidFormatException rootCause = mock(InvalidFormatException.class);
        HttpInputMessage mockInput = new MockHttpInputMessage("{}".getBytes());
        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("invalid", rootCause, mockInput);
        when(builder.buildError(rootCause, request))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(rootCause, request);
    }

    @Test
    void handleWebFluxBindingErrors_withoutInvalidFormatCause_shouldReturnGenericBadRequest() {
        ServerWebInputException ex = new ServerWebInputException("Bad JSON");
        when(builder.buildError(ex, request))
                .thenReturn(dummyResponse);
        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(builder).buildError(ex, request);
    }
    @Test
    void handleCustomError_shouldDelegateAndToCustomBuildAndReturnExceptionStatusCode(){

        class GenericNotFoundException extends BaseApiException {
            GenericNotFoundException(String arg){
                super(arg, ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"error.notFound",new Object[]{arg}));
            }
        }
        GenericNotFoundException ex = new GenericNotFoundException("GenericNotFound");
        when (builder.buildCustomExceptionError(ex,request))
                .thenReturn(dummyResponse);

        ResponseEntity<APIErrorResponse> response = handler.handleApiCustomException(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(builder).buildCustomExceptionError(ex,request);
    }
}
