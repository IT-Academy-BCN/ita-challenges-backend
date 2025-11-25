package com.itachallenge.submission.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserSubmissionRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenAllFieldsAreValid() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .status("SUBMITTED_COMPLETE")
                .submissionText("my submission text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertTrue(violations.isEmpty(), "DTO should be valid");
    }

    @Test
    void shouldFailValidation_whenUserIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId("not-a-uuid")
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .status("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Invalid UUID")),
                "Expected Invalid UUID error"
        );
    }

    @Test
    void shouldFailValidation_whenChallengeIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId("1234")
                .languageId(UUID.randomUUID().toString())
                .status("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("challengeId")),
                "Expected challengeId validation error"
        );
    }

    @Test
    void shouldFailValidation_whenLanguageIdIsInvalidUUID() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId("xxx")
                .status("ANY")
                .submissionText("text")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("languageId")),
                "Expected languageId validation error"
        );
    }

    @Test
    void shouldFailValidation_whenSolutionTextIsBlank() {
        UserSubmissionRequestDto dto = UserSubmissionRequestDto.builder()
                .userId(UUID.randomUUID().toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .status("ANY")
                .submissionText("   ")
                .build();

        Set<ConstraintViolation<UserSubmissionRequestDto>> violations =
                validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().contains("Submission text is required")),
                "Expected NotBlank validation error"
        );
    }
}

