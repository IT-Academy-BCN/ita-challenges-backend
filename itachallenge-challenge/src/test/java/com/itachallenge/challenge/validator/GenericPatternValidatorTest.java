package com.itachallenge.challenge.validator;

import com.itachallenge.challenge.annotations.ValidGenericPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GenericPatternValidatorTest {
    private GenericPatternValidator validator;
    @Mock
    private ValidGenericPattern constraintAnnotation;

    @BeforeEach
    void setUp() {
        validator = new GenericPatternValidator();
        Mockito.when(constraintAnnotation.pattern()).thenReturn("^\\d{1,9}$");
        Mockito.when(constraintAnnotation.message()).thenReturn("Invalid value");

        validator.initialize(constraintAnnotation);
    }

    @Test
    void isValid() {
        boolean isValid = validator.isValid("123456", null);

        assertTrue(isValid);
    }

}
