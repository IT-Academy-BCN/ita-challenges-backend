package com.itachallenge.user.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericUUIDValidatorTest {

    private GenericUUIDValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = new GenericUUIDValidator();

        // Simulate @Value injection manually
        String uuidRegex = "^[a-fA-F0-9]{24}$"; // Example MongoDB ObjectId pattern
        validator.UUID_PATTERN = Pattern.compile(uuidRegex);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    }

    @Test
    void testValidUUID_ShouldReturnTrue() {
        String validUUID = "507f1f77bcf86cd799439011"; // Example valid MongoDB ObjectId

        assertTrue(validator.isValid(validUUID, context));
    }

    @Test
    void testNullUUID_ShouldReturnFalseAndSetMessage() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Invalid UUID");

        boolean result = validator.isValid(null, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Invalid UUID: value is null");
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testInvalidUUID_ShouldReturnFalseAndSetMessage() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Invalid UUID");
        String invalidUUID = "invalid-uuid-1234";

        boolean result = validator.isValid(invalidUUID, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Invalid UUID: " + invalidUUID);
        verify(violationBuilder).addConstraintViolation();
    }

    @Test
    void testEmptyUUID_ShouldReturnFalseAndSetMessage() {
        when(context.getDefaultConstraintMessageTemplate()).thenReturn("Invalid UUID");
        String emptyUUID = "";

        boolean result = validator.isValid(emptyUUID, context);

        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Invalid UUID: ");
        verify(violationBuilder).addConstraintViolation();
    }
}

