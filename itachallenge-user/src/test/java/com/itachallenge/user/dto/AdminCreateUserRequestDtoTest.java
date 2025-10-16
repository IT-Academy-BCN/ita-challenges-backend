package com.itachallenge.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Set;

import org.springframework.context.MessageSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminCreateUserRequestDtoTest {

    private static Validator validator;


    @BeforeAll
    static void setupValidator() {
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
    void whenUsernameIsBlank_thenValidationFails() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("");

        Set<ConstraintViolation<AdminCreateUserRequestDto>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("The username must not be blank.", violations.iterator().next().getMessage());
    }
}
