package com.itachallenge.user.exception;

import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.GlobalExceptionHandler;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Integration test suite for UserExceptionHandler.
 * Focuses on validation-related exceptions using input DTOs.
 */
@SpringBootTest
class UserExceptionHandlerIntegrationTest {


    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private Validator validator;

    @Autowired
    private MessageSource messageSource;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setup() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn("/api/users/test");
    }

    // 1️⃣ AdminCreateUserRequestDto - Missing username
    @Test
    void shouldHandleMethodArgumentNotValidException_onAdminCreateUserRequestDto() throws Exception {
        AdminCreateUserRequestDto dto = new AdminCreateUserRequestDto();
        dto.setUsername(""); // invalid: @NotBlank

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "adminCreateUserRequestDto");
        validator.validate(dto).forEach(v ->
                bindingResult.rejectValue(
                        v.getPropertyPath().toString(),
                        v.getMessageTemplate(),
                        v.getMessage()
                )
        );

        Method method = UserExceptionHandlerIntegrationTest.class
                .getMethod("dummyAdminMethod", AdminCreateUserRequestDto.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(method, bindingResult);

        APIErrorResponse response = globalExceptionHandler.handleMethodArgumentNotValidException(ex, mockRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("Validation");
        assertThat(response.getErrors()).isNotEmpty();

        // Check that username appears among the invalid fields
        assertThat(response.getErrors().getFirst().getField()).isEqualTo("username");

        // Confirm the localized message exists
        String localized = messageSource.getMessage("adminCreateUser.username.notBlank", null, Locale.ENGLISH);
        assertThat(localized).contains("must not be blank");
    }

    public void dummyAdminMethod(AdminCreateUserRequestDto dto) { /* for reflection */ }

    // 2️⃣ UserSolutionRequestDto - multiple invalid fields
    @Test
    void shouldHandleMethodArgumentNotValidException_onUserSolutionRequestDto() throws Exception {
        UserSolutionRequestDto dto = UserSolutionRequestDto.builder()
                .userId("invalid-uuid")
                .challengeId("1234")
                .languageId("bad")
                .solutionText("") // violates @NotBlank
                .status("PENDING")
                .build();

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "userSolutionRequestDto");
        validator.validate(dto).forEach(v ->
                bindingResult.rejectValue(
                        v.getPropertyPath().toString(),
                        v.getMessageTemplate(),
                        v.getMessage()
                )
        );

        Method method = UserExceptionHandlerIntegrationTest.class
                .getMethod("dummyUserMethod", UserSolutionRequestDto.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(method, bindingResult);

        APIErrorResponse response = globalExceptionHandler.handleMethodArgumentNotValidException(ex, mockRequest).getBody();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getMessage()).contains("Validation");
        assertThat(response.getErrors()).isNotEmpty();

        // Expect invalid UUID and text errors
        assertThat(response.getErrors().stream()
                .anyMatch(err -> err.getField().equals("solutionText") || err.getField().equals("userId")))
                .isTrue();

        String localized = messageSource.getMessage("user.solution.text.notEmpty", null, Locale.ENGLISH);
        assertThat(localized).contains("cannot be empty");
    }

    public void dummyUserMethod(UserSolutionRequestDto dto) { /* for reflection */ }
}
