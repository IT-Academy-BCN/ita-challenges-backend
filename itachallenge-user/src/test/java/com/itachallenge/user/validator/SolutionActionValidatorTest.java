package com.itachallenge.user.validator;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SolutionActionValidatorTest {
    private SolutionActionValidator validator;
    private ConstraintValidatorContext context;


    @BeforeEach
    void setUp() {
        validator = new SolutionActionValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    @DisplayName("Should return true for valid cases, in upper or lower case")
    void shouldReturnTrueForValidActions() {
        assertTrue(validator.isValid("SAVE", context));
        assertTrue(validator.isValid("save", context));
        assertTrue(validator.isValid("SUBMIT", context));
        assertTrue(validator.isValid("GIVE_UP", context));
    }


    @Test
    @DisplayName("Should return false for invalid cases")
    void shouldReturnFalseForInvalidActions() {
        assertFalse(validator.isValid("INVALID", context));
        assertFalse(validator.isValid("submittt", context));
        assertFalse(validator.isValid("give_up_button", context));
        assertFalse(validator.isValid("submit!", context));
    }

    @Test
    @DisplayName("Should return false for case null or empty spaces")
    void shouldReturnFalseWhenNullOrEmpty() {
        assertFalse(validator.isValid(null, context));
        assertFalse(validator.isValid("", context));
        assertFalse(validator.isValid("   ", context));
    }


}



