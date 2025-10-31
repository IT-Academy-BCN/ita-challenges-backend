package com.itachallenge.errorcore.builder;

import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.dto.FieldErrorDto;
import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.codec.DecodingException;
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
 * Integration-style tests for ErrorResponseBuilder using the real core-messages.properties file.
 */
class ErrorResponseBuilderTest {

    private ErrorResponseBuilder builder;
    private HttpServletRequest request;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static class DummyController {

        public void testMethod(Integer age) {
            // Intentionally left blank: this method is only used for reflection-based testing of type mismatches.
        }

        public void acceptTestDto(@Valid TestDto dto) {
            // Intentionally empty: used to trigger @Valid validation in tests.
        }
    }

    static class TestDto {
        @NotNull(message = "email cannot be null")
        private final String email;
        public TestDto(String email) { this.email = email; }
        public String getEmail() { return email; }
    }

    @BeforeEach
    void setUp() {
        // Use the *real* global core-messages.properties file
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:core-messages"); // points to src/main/resources/core-messages.properties
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
        APIErrorResponse response = builder.buildError(new DecodingException("failure decoding"), request);

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
    void buildStatusErrorResponse_shouldUseAppropriateMessageWhen404() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        APIErrorResponse response = builder.buildStatusErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("The requested resource could not be found.");
    }

    @Test
    void buildStatusErrorResponse_shouldUseAppropriateMessageWhen400() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");

        APIErrorResponse response = builder.buildStatusErrorResponse(ex, request);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("The request could not be understood by the server.");
    }


    @Test
    void buildNotFoundError_shouldReturnNotFoundResponse() {
        class GenericNotFoundException extends BaseApiException{
            GenericNotFoundException(String arg){
                super(ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"error.notFound",new Object[]{arg}));
            }
        }
        // Given
        GenericNotFoundException ex = new GenericNotFoundException("Resource not found");

        // When
        APIErrorResponse response = builder.buildCustomExceptionError(ex,request);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Resource not found");
        assertThat(response.getPath()).isEqualTo("/api/test");
        assertThat(response.getTimestamp()).isNotNull();
    }

}
