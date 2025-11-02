package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.ChallengeCreateDto;
import com.itachallenge.challenge.dto.ResourceDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private GlobalExceptionHandler handler;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private Validator validator;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setup() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/test");
    }

    // 1️⃣ MethodArgumentNotValidException
    @Test
    void shouldHandleMethodArgumentNotValidException_usingLocalizedMessages() throws Exception {
        // Create an invalid DTO (missing @NotEmpty fields)
        ChallengeCreateDto dto = ChallengeCreateDto.builder().build();

        // Bind and validate manually
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "challengeCreateDto");
        validator.validate(dto).forEach(v ->
                bindingResult.rejectValue(
                        v.getPropertyPath().toString(),
                        v.getMessageTemplate(),
                        v.getMessage()
                )
        );

        // Build a synthetic MethodArgumentNotValidException
        Method method = GlobalExceptionHandlerIntegrationTest.class.getMethod("emptyMethod", ChallengeCreateDto.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(method, bindingResult);

        // Call the handler
        APIErrorResponse response = handler.handleMethodArgumentNotValidException(ex, mockRequest).getBody();

        // Assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("Validation");
        assertThat(response.getErrors()).isNotEmpty();

        // Bonus: check one of the actual field errors
        assertThat(response.getErrors().getFirst().getField()).isIn(
                "challengeTitle", "description", "language", "solution", "tags","topic"
        );
    }


    public void emptyMethod(ChallengeCreateDto dto) { /* This is an empty method to test */ }

    // 2️⃣ ConstraintViolationException
    @Test
    void shouldHandleConstraintViolationException_usingLocalizedMessages() {
        // Create an invalid object
        SolutionDto dto = new SolutionDto(null, "", UUID.randomUUID());

        Set<ConstraintViolation<SolutionDto>> violations = validator.validate(dto);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        // Call the handler
        APIErrorResponse response = handler.handleValidationExceptions(ex, mockRequest).getBody();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("validation");
        assertThat(response.getErrors()).isNotEmpty();
        String localized = messageSource.getMessage("solution.text.notEmpty", null, Locale.ENGLISH);
        assertThat(localized).contains("cannot be empty");
    }

    // 3️⃣ MethodArgumentTypeMismatchException
    @Test
    void shouldHandleTypeMismatchException_usingLocalizedMessages() throws Exception {

        Method method = ResourceDto.class.getMethod("setTopic", Topic.class);
        MethodParameter param = new MethodParameter(method, 0);
        // Create exception with a non-null parameter
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "notAValidTopic",           // the invalid value
                Topic.class,                // required type
                "topic",                    // parameter name
                param,                      // <-- real MethodParameter
                new IllegalArgumentException("Invalid topic") // root cause
        );

        APIErrorResponse response = handler.handleTypeMismatchException(ex, mockRequest).getBody();

        // Assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("Parameter").doesNotContain("unknown");
        assertThat(response.getErrors()).isNotEmpty();
        assertThat(response.getErrors().getFirst().getField()).isEqualTo("topic");
    }

    // 3️⃣ ChallengeNotFoundException
    @Test
    void shouldHandleChallengeNotFoundException_usingLocalizedMessages() {

        ChallengeNotFoundException ex = new ChallengeNotFoundException("challenge not found");

        APIErrorResponse response = handler.handleApiCustomException(ex, mockRequest).getBody();
        // Assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getMessage()).contains("challenge not found");
        assertThat(response.getErrors()).isNull();
    }

    @Test
    void shouldBadUUIDException_usingLocalizedMessages() {

        BadUUIDException ex = new BadUUIDException("invalid uuid");

        APIErrorResponse response = handler.handleApiCustomException(ex, mockRequest).getBody();
        // Assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("invalid uuid");
        assertThat(response.getErrors()).isNull();
    }

}

