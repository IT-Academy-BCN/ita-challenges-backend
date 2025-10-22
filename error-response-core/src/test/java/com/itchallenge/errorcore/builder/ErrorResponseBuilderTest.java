package com.itchallenge.errorcore.builder;

import com.itchallenge.errorcore.dto.APIErrorResponse;
import com.itchallenge.errorcore.dto.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration-style tests for ErrorResponseBuilder using the real message.properties file.
 */
class ErrorResponseBuilderTest {

    private ErrorResponseBuilder builder;
    private HttpServletRequest request;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static class DummyController {
        public void testMethod(Integer age) {}
        public void acceptTestDto(@Valid TestDto dto) {}
    }

    static class TestDto {
        @NotNull(message = "email cannot be null")
        private final String email;
        public TestDto(String email) { this.email = email; }
        public String getEmail() { return email; }
    }

    @BeforeEach
    void setUp() {
        // Use the *real* global message.properties file
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:message"); // points to src/main/resources/message.properties
        messageSource.setDefaultEncoding("UTF-8");

        builder = new ErrorResponseBuilder(messageSource);
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ------------------------------------------------------------
    // buildError
    // ------------------------------------------------------------
    @Test
    void buildError_shouldBuildBasicResponseWithRealMessage() {
        APIErrorResponse response = builder.buildError(HttpStatus.BAD_REQUEST, "validation.bad_request", request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).contains("Invalid or malformed request");
        assertThat(response.getPath()).isEqualTo("/api/test");
    }

    // ------------------------------------------------------------
    // buildTypeMismatchErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildTypeMismatchErrorResponse_shouldIncludeFieldInformation() throws Exception {
        Method method = DummyController.class.getDeclaredMethod("testMethod", Integer.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);

        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "age", methodParameter, new IllegalArgumentException("type mismatch")
        );

        APIErrorResponse response = builder.buildTypeMismatchErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrors()).hasSize(1);
        FieldErrorDto error = response.getErrors().getFirst();
        assertThat(error.getField()).isEqualTo("age");
        assertThat(error.getMessage()).contains("Parameter 'age' has invalid value 'abc'. Expected type: Integer");
        assertThat(error.getObjectName()).isEqualTo("DummyController");
    }

    // ------------------------------------------------------------
    // buildConstraintViolationErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildConstraintViolationErrorResponse_shouldBuildWithViolations() {
        // Create a real constraint violation manually
        ConstraintViolation<?> violation = validator.validate(new TestDto(null)).iterator().next();
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        APIErrorResponse response = builder.buildConstraintViolationErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).contains("One or more request parameters failed validation");
        assertThat(response.getErrors()).isNotEmpty();
        assertThat(response.getErrors().getFirst().getMessage()).contains("Validation failed for parameter");
    }

    // ------------------------------------------------------------
    // buildArgumentNotValidErrorResponse
    // ------------------------------------------------------------
    @Test
    void buildArgumentNotValidErrorResponse_shouldExtractFieldErrorsWithRealMessageSource() throws Exception {
        TestDto invalidDto = new TestDto(null);
        BindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "testDto");

        // Manually perform validation and fill the BindingResult
        validator.validate(invalidDto).forEach(v ->
                bindingResult.rejectValue("email", null, v.getMessage()));

        Method method = DummyController.class.getDeclaredMethod("acceptTestDto", TestDto.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        APIErrorResponse response = builder.buildArgumentNotValidErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getErrors()).hasSize(1);
        assertThat(response.getErrors().getFirst().getField()).isEqualTo("email");
        assertThat(response.getErrors().getFirst().getMessage()).isEqualTo("email cannot be null");
        assertThat(response.getMessage()).contains("object 'testDto'");
    }

    // ------------------------------------------------------------
    // buildStatusErrorResponse (ResponseStatusException)
    // ------------------------------------------------------------
    @Test
    void buildStatusErrorResponse_shouldUseValidationFailedWhenNoArgs() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        APIErrorResponse response = builder.buildStatusErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("Not Found");
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
