package com.itachallenge.challenge.helper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ParameterValidatorTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeClass
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterClass
    public static void close() {
        validatorFactory.close();
    }

    @Test
    @DisplayName("Validate pagination parameters")
    public void parameterValidatorTest() {
        ParameterValidator parameterValidator = new ParameterValidator();
        parameterValidator.setPageNumber("a");
        parameterValidator.setPageSize("b");
        Set<ConstraintViolation<ParameterValidator>> violations = validator.validate(parameterValidator);
        assertFalse(violations.isEmpty());
    }
    @Test
    public void getValidPageNumberTest_ValidPageNumber() {
        ParameterValidator parameterValidator = new ParameterValidator();
        String expectedPageNumber = "1";
        Optional<Integer> result = parameterValidator.getValidPageNumber(expectedPageNumber);
        assertEquals(Optional.of(1), result);
    }
    @Test
    public void getValidPageNumberTest_NotValidPageNumber() {
        ParameterValidator parameterValidator = new ParameterValidator();
        String notValidPageNumber = "a";
        Optional<Integer> result = parameterValidator.getValidPageNumber(notValidPageNumber);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void getValidPageSizeTest_ValidPageSize() {
        ParameterValidator parameterValidator = new ParameterValidator();
        String expectedPageSize = "3";
        Optional<Integer> result = parameterValidator.getValidPageSize(expectedPageSize);
        assertEquals(Optional.of(3), result);
    }
    @Test
    public void getValidPageSizeTest_NotValidPageSize() {
        ParameterValidator parameterValidator = new ParameterValidator();
        String notValidPageSize= "a";
        Optional<Integer> result = parameterValidator.getValidPageNumber(notValidPageSize);
        assertEquals(Optional.empty(), result);
    }
}
