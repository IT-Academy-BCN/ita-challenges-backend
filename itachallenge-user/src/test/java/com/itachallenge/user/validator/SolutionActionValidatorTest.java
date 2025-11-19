package com.itachallenge.user.validator;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SolutionActionValidatorTest {
    private SolutionActionValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        validator = new SolutionActionValidator();
    }
}
