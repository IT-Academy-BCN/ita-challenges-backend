package com.itachallenge.user.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SolutionActionValidatorTest {

    private SolutionActionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SolutionActionValidator();
    }

    @ParameterizedTest
    @DisplayName("Should return true for valid actions (upper and lower case)")
    @ValueSource(strings = {
            "SAVE", "GIVE_UP", "SUBMIT", "save", "give_Up", "SUbMIT"
            })
    void shouldReturnTrueForValidActions(String action) {
        assertTrue(validator.isValid(action, null));
    }

    @ParameterizedTest
    @DisplayName("Should return false for invalid actions")
    @ValueSource(strings = {
            "INVALID", "submittt", "save_me", "give_up!", "@€!^*%"
    })
    void shouldReturnFalseForInvalidActions(String action) {
        assertFalse(validator.isValid(action, null));
    }

    @ParameterizedTest
    @DisplayName("Should return false for empty or blank inputs")
    @ValueSource(strings = {
            "   ", "\t", "\n", "  \t  \n  "
    })
    void shouldReturnFalseForEmptyOrBlank(String action) {
        assertFalse(validator.isValid(action, null));
    }

    @ParameterizedTest
    @DisplayName("Should return false when value contains valid action but padded with spaces")
    @ValueSource(strings = {
            " SAVE", "SUBMIT  ", "  GIVE_UP  ", "\tSAVE", "SAVE\n"
    })
    void shouldReturnFalseForSpacedValues(String action) {
        assertFalse(validator.isValid(action, null));
    }

    @Test
    @DisplayName("Should return false for null input")
    void shouldReturnFalseForNull() {
        assertFalse(validator.isValid(null, null));
    }




}
