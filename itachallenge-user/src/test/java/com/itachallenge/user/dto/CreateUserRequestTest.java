package com.itachallenge.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateUserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenUsernameIsBlank_thenValidationFails() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("");

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals("Username must not be blank", violations.iterator().next().getMessage());
    }
}
