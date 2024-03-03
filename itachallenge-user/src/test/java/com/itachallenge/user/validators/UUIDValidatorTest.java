package com.itachallenge.user.validators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UUIDValidatorTest {

    @Test
    void testValidUUID() {
        assertTrue(UUIDValidator.isValidUUID("550e8400-e29b-41d4-a716-446655440000"));
    }

    @Test
    void testInvalidUUID() {
        assertFalse(UUIDValidator.isValidUUID("not_a_uuid"));
    }

    @Test
    void testNullUUID() {
        assertFalse(UUIDValidator.isValidUUID(null));
    }
}
