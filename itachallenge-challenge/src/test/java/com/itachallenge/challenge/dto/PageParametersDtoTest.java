package com.itachallenge.challenge.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.Assert.assertFalse;

@SpringBootTest
public class PageParametersDtoTest {

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
        PageParametersDto pagingParametersDTO = new PageParametersDto();
        pagingParametersDTO.setPageNumber("a");
        pagingParametersDTO.setPageSize("b");
        Set<ConstraintViolation<PageParametersDto>> violations = validator.validate(pagingParametersDTO);
        assertFalse(violations.isEmpty());
    }
}
