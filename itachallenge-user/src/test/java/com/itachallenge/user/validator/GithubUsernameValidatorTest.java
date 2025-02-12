package com.itachallenge.user.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GithubUsernameValidatorTest {

    private GithubUsernameValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new GithubUsernameValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void shouldReturnTrueForValidUsername() {
        assertTrue(validator.isValid("validUsername", context));
        assertTrue(validator.isValid("user-123", context));
        assertTrue(validator.isValid("A1-B2-C3", context));
    }

    @Test
    void shouldReturnFalseForNullUsername() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void shouldReturnFalseForEmptyString() {
        assertFalse(validator.isValid("", context));
    }

    @Test
    void shouldReturnFalseForTooLongUsername() {
        String longUsername = "a".repeat(40);
        assertFalse(validator.isValid(longUsername, context));
    }

    @Test
    void shouldReturnFalseForInvalidCharacters() {
        assertFalse(validator.isValid("invalid_username!", context));
        assertFalse(validator.isValid("invalid@username", context));
        assertFalse(validator.isValid("user name", context));
    }

    @Test
    void shouldReturnFalseForUsernameStartingOrEndingWithHyphen() {
        assertFalse(validator.isValid("-invalidUser", context));
        assertFalse(validator.isValid("invalidUser-", context));
    }

    @Test
    void shouldReturnTrueForMinimumLengthUsername() {
        assertTrue(validator.isValid("a", context));
    }

    @Test
    void shouldReturnTrueForMaximumLengthUsername() {
        String maxUsername = "a".repeat(39);
        assertTrue(validator.isValid(maxUsername, context));
    }
}

