package com.itchallenge.errorcore.builder;

import com.itchallenge.errorcore.dto.APIErrorResponse;
import com.itchallenge.errorcore.dto.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorResponseBuilderTest {

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpServletRequest request;

    private ErrorResponseBuilder builder;

    private static class DummyController {
        public void testMethod(Integer age) {}
    }

    @BeforeEach
    void setUp() {
        builder = new ErrorResponseBuilder(messageSource);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ------------------------------------------------------------
    // buildError
    // ------------------------------------------------------------
    @Test
    void buildError_shouldBuildBasicResponse() {
        when(messageSource.getMessage(eq("some.message"), any(), any(Locale.class)))
                .thenReturn("Resolved message");

        APIErrorResponse response = builder.buildError(HttpStatus.BAD_REQUEST, "some.message", request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Resolved message");
        assertThat(response.getPath()).isEqualTo("/api/test");
    }

    // ------------------------------------------------------------
    // buildTypeMismatchErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildTypeMismatchErrorResponse_shouldIncludeFieldInformation() {
        // Mock MethodParameter and containing class
        Method method = DummyController.class.getDeclaredMethods()[0];
        MethodParameter methodParameter = new MethodParameter(method, 0);

        // Create the exception with the mocked MethodParameter
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "age", methodParameter, new IllegalArgumentException("type mismatch")
        );

        // Mock message resolution
        when(messageSource.getMessage(eq("validation.type_mismatch"), any(), any(Locale.class)))
                .thenReturn("Parameter 'age' has invalid value 'abc'. Expected type: Integer.");

        // Execute
        APIErrorResponse response = builder.buildTypeMismatchErrorResponse(ex, request);

        // Assert
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrors()).hasSize(1);
        FieldErrorDto error = response.getErrors().getFirst();
        assertThat(error.getField()).isEqualTo("age");
        assertThat(error.getMessage()).contains("Parameter 'age'");
        assertThat(error.getObjectName()).isEqualTo("DummyController");
    }

    // ------------------------------------------------------------
    // buildConstraintViolationErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildConstraintViolationErrorResponse_shouldBuildWithViolations() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("user.email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");
        when(violation.getRootBeanClass()).thenReturn((Class) String.class);

        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        when(messageSource.getMessage(eq("validation.constraint.detailed"), any(), any(Locale.class)))
                .thenReturn("Validation failed for parameter 'email': must not be null");
        when(messageSource.getMessage(eq("validation.constraint"), any(), any(Locale.class)))
                .thenReturn("One or more request parameters failed validation.");

        APIErrorResponse response = builder.buildConstraintViolationErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrors()).hasSize(1);
        assertThat(response.getMessage()).contains("or more request parameters failed");
        assertThat(response.getErrors().getFirst().getField()).isEqualTo("email");
        assertThat(response.getErrors().getFirst().getMessage()).isEqualTo("Validation failed for parameter 'email': must not be null");
    }

    // ------------------------------------------------------------
    // buildArgumentNotValidErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildArgumentNotValidErrorResponse_shouldExtractFieldErrors() throws Exception {
        FieldError fieldError = new FieldError("userDto", "email", "must not be null");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getObjectName()).thenReturn("userDto");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        Method method = getClass().getMethod("setUp");
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        when(messageSource.getMessage(any(FieldError.class), any(Locale.class)))
                .thenReturn("Email must not be null");
        when(messageSource.getMessage(eq("validation.argument_not_valid"), any(), any(Locale.class)))
                .thenReturn("Validation failed for object 'userDto'.");

        APIErrorResponse response = builder.buildArgumentNotValidErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrors()).hasSize(1);
        assertThat(response.getErrors().get(0).getField()).isEqualTo("email");
        assertThat(response.getMessage()).contains("userDto");
    }

    // ------------------------------------------------------------
    // buildStatusErrorResponse (ResponseStatusException)
    // ------------------------------------------------------------
    @Test
    void buildStatusErrorResponse_shouldUseValidationFailedWhenNoArgs() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        APIErrorResponse response = builder.buildStatusErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("Validation failed");
    }

    @Test
    void buildStatusErrorResponse_shouldJoinDetailMessageArguments() {
        ResponseStatusException ex = mock(ResponseStatusException.class);
        when(ex.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(ex.getDetailMessageArguments()).thenReturn(new Object[]{"x", "a", "b", "c"});

        APIErrorResponse response = builder.buildStatusErrorResponse(ex, request);

        assertThat(response.getMessage()).isEqualTo("a, b, c");
        assertThat(response.getStatus()).isEqualTo(400);
    }
}
