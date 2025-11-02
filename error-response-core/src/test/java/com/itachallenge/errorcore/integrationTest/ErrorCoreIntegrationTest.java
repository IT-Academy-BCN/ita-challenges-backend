package com.itachallenge.errorcore.integrationTest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.config.ErrorHandlingConfig;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import com.itachallenge.errorcore.exceptionhandler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ErrorHandlingConfig.class })
class ErrorCoreIntegrationTest {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ErrorResponseBuilder errorResponseBuilder;

    @Autowired
    private GlobalExceptionHandler handler;

    private HttpServletRequest request;

    @BeforeEach
    void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // --- Dummy Controller & DTO for MethodParameter simulation ---
    static class EmptyController {
        public void handle(EmptyDto dto) {
            /* the method is just for testing -  hence its emptyness*/
        }
    }

    static class EmptyDto {
        @SuppressWarnings("unused")
        private String name;
    }

    // ----------------------------------------------------------------
    // 1️⃣  handleAny(Exception)
    // ----------------------------------------------------------------
    @Test
    void handleAny_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error");
        ResponseEntity<APIErrorResponse> response = handler.handleAny(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
    }

    // ----------------------------------------------------------------
    // 2️⃣  handleApiCustomException(BaseApiException)
    // ----------------------------------------------------------------
    @Test
    void handleApiCustomException_shouldReturnCustomStatusAndText() {

        BaseApiException ex = mock(BaseApiException.class);
        when(ex.getMessage()).thenReturn("status.exception.401");
        when(ex.getInfo()).thenReturn(ApiCustomErrorInfo.of(HttpStatus.FORBIDDEN,"status.exception.401",new Object[]{}));
        ResponseEntity<APIErrorResponse> response = handler.handleApiCustomException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("You are not authorized to perform this action.");
    }

    // ----------------------------------------------------------------
    // 3️⃣  handleIllegalArgument(IllegalArgumentException)
    // ----------------------------------------------------------------
    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<APIErrorResponse> response = handler.handleIllegalArgument(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Illegal argument in request.");
    }

    // ----------------------------------------------------------------
// 4️⃣  handleValidationExceptions(ConstraintViolationException)
// ----------------------------------------------------------------
    @Test
    void handleValidationExceptions_shouldReturnBadRequest_withSingleViolation() {
        // Given
        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn("testField");

        ConstraintViolation<EmptyDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("must not be null");
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(violation.getRootBeanClass()).thenReturn(EmptyDto.class);

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", violations);

        // When
        ResponseEntity<APIErrorResponse> response = handler.handleValidationExceptions(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("One or more request parameters failed validation.");
        assertThat(response.getBody().getErrors().getFirst().getObjectName()).isEqualTo("EmptyDto");
        assertThat(response.getBody().getErrors().getFirst().getMessage()).isEqualTo("Validation failed for parameter 'testField': must not be null");
    }

    @Test
    void handleTypeMismatch_shouldReturnBadRequest() throws NoSuchMethodException {
        // Given: we need a MethodParameter for a method argument of type Integer
        Method method = EmptyController.class.getDeclaredMethod("handle", EmptyDto.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", EmptyDto.class, "dto", parameter, new IllegalArgumentException());

        // When
        ResponseEntity<APIErrorResponse> response = handler.handleTypeMismatchException(ex, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Parameter 'dto' has invalid value 'abc'. Expected type: EmptyDto");
    }


    // ----------------------------------------------------------------
    // 6️⃣  handleMethodArgumentNotValidException(MethodArgumentNotValidException)
    // ----------------------------------------------------------------
    @Test
    void handleMethodArgumentNotValid_shouldReturnBadRequest() throws NoSuchMethodException {
        // Fake method parameter
        Method method = EmptyController.class.getDeclaredMethod("handle", EmptyDto.class);
        MethodParameter param = new MethodParameter(method, 0);

        // Simulate validation error
        EmptyDto dto = new EmptyDto();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "dummyDto");
        bindingResult.addError(new FieldError("emptyDto", "name", dto, false,
                new String[]{"validation.bad_request"}, null, null));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(param, bindingResult);

        ResponseEntity<APIErrorResponse> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrors().size()).isEqualTo(1);
        assertThat(response.getBody().getErrors().getFirst().getMessage()).isEqualTo("Invalid or malformed request.");
    }

    // ----------------------------------------------------------------
    // 7️⃣  handleResponseStatusException(ResponseStatusException)
    // ----------------------------------------------------------------
    @Test
    void handleResponseStatusException_shouldReturnStatusFromException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");

        ResponseEntity<APIErrorResponse> response = handler.handleResponseStatusException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("The requested resource could not be found.");
    }

    // ----------------------------------------------------------------
    // 8️⃣  handleInvalidFormat(InvalidFormatException)
    // ----------------------------------------------------------------
    @Test
    void handleInvalidFormat_shouldReturnBadRequest() {
        InvalidFormatException ex = new InvalidFormatException(null, "Bad format", "123", Integer.class);

        ResponseEntity<APIErrorResponse> response = handler.handleInvalidFormat(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid or malformed request.");
    }

    // ----------------------------------------------------------------
    // 9️⃣  handleWebFluxBindingErrors(HttpMessageNotReadableException, etc.)
    // ----------------------------------------------------------------
    @Test
    void handleWebFluxBindingErrors_shouldReturnBadRequest() {
        DecodingException ex = new DecodingException("Unreadable message");
        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid or malformed request.");
    }

    @Test
    void handleWebFluxBindingErrors_shouldHandleNestedInvalidFormat() {
        HttpInputMessage message = mock(HttpInputMessage.class);
        InvalidFormatException root = new InvalidFormatException(null, "Invalid format", "abc", Integer.class);
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Outer", root,message);

        ResponseEntity<APIErrorResponse> response = handler.handleWebFluxBindingErrors(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid or malformed request.");
    }
}
