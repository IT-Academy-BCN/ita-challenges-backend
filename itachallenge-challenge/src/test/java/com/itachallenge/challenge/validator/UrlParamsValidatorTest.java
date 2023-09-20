package com.itachallenge.challenge.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
class UrlParamsValidatorTest {

    ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    Validator validator = validatorFactory.getValidator();

    @Test
    @DisplayName("Validate pagination parameters")
    void parameterValidatorTest() {
        UrlParamsValidator urlParamsValidator = new UrlParamsValidator();
        urlParamsValidator.setPage("a");
        urlParamsValidator.setSize("b");
        Set<ConstraintViolation<UrlParamsValidator>> violations = validator.validate(urlParamsValidator);
        Assertions.assertFalse(violations.isEmpty());
    }

}

