package com.itachallenge.user.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolutionActionValidatorTest {

    private SolutionActionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SolutionActionValidator();
    }

    @Test
    @DisplayName("Should return true for valid actions (upper and lower case)")
    void shouldReturnTrueForValidActions() {
        assertTrue(validator.isValid("SAVE", null));
        assertTrue(validator.isValid("save", null));
        assertTrue(validator.isValid("SUBMIT", null));
        assertTrue(validator.isValid("GIVE_UP", null));
    }

    @Test
    @DisplayName("Should return false for invalid actions")
    void shouldReturnFalseForInvalidActions() {
        assertFalse(validator.isValid("INVALID", null));
        assertFalse(validator.isValid("submittt", null));
        assertFalse(validator.isValid("save_me", null));
        assertFalse(validator.isValid("submit!", null));
    }

    @Test
    @DisplayName("Should return false for null, empty, or blank inputs")
    void shouldReturnFalseForNullOrBlank() {
        assertFalse(validator.isValid(null, null));   // null case
        assertFalse(validator.isValid("", null));     // empty string
        assertFalse(validator.isValid("   ", null));  // blank spaces
    }

    @Test
    @DisplayName("Should return false when value contains valid action but padded with spaces")
    void shouldReturnFalseForSpacedValues() {
        assertFalse(validator.isValid(" SAVE ", null));
        assertFalse(validator.isValid("  SUBMIT", null));
        assertFalse(validator.isValid("GIVE_UP  ", null));
    }
}
