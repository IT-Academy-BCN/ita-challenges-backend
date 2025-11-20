package com.itachallenge.challenge.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SolutionDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        // ✅ Force locale for predictable message interpolation
        Locale.setDefault(Locale.ENGLISH);

        // ✅ Create a Validator that explicitly loads messages.properties
        ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(
                        new ResourceBundleMessageInterpolator(
                                new PlatformResourceBundleLocator("messages")
                        )
                )
                .buildValidatorFactory();

        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should fail validation when solutionText is empty")
    void shouldFailWhenSolutionTextEmpty() {
        SolutionDto dto = new SolutionDto(
                UUID.randomUUID(),
                "", // invalid: empty
                UUID.randomUUID()
        );

        Set<ConstraintViolation<SolutionDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty()
                .anyMatch(v -> v.getMessage().equals("The solution text cannot be empty."));
    }

    @Test
    @DisplayName("Should fail when language or challenge UUIDs are invalid")
    void shouldFailForInvalidUUIDs() {
        // Simulate invalid UUIDs (null)
        SolutionDto dto = new SolutionDto(null, "some text", null);

        Set<ConstraintViolation<SolutionDto>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty()
                .anyMatch(v -> v.getMessage().equals("The language ID must be a valid UUID."))
                .anyMatch(v -> v.getMessage().equals("The challenge ID must be a valid UUID."));
    }

    @Test
    @DisplayName("Should pass validation for a valid SolutionDto")
    void shouldPassWhenValid() {
        SolutionDto dto = new SolutionDto(
                UUID.randomUUID(),
                "Valid solution text",
                UUID.randomUUID()
        );
        dto.setIdChallenge(UUID.randomUUID());

        Set<ConstraintViolation<SolutionDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}