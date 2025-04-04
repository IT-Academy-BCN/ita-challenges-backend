package com.itachallenge.challenge.helper;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SafeDataParseTest {

    @Test
    void whenValidUUID_thenReturnsOptionalWithUUID() {
        String raw = "123e4567-e89b-12d3-a456-426614174000";
        Optional<UUID> result = SafeDataParse.safeParseUUID(raw);
        assertTrue(result.isPresent());
        assertEquals(UUID.fromString(raw), result.get());
    }

    @Test
    void whenNullInput_thenReturnsEmptyOptional() {
        Optional<UUID> result = SafeDataParse.safeParseUUID(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenBlankInput_thenReturnsEmptyOptional() {
        Optional<UUID> result = SafeDataParse.safeParseUUID("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void whenLiteralNullInput_thenReturnsEmptyOptional() {
        Optional<UUID> result = SafeDataParse.safeParseUUID("null");
        assertTrue(result.isEmpty());
    }

    @Test
    void whenInvalidUUID_thenReturnsEmptyOptional() {
        Optional<UUID> result = SafeDataParse.safeParseUUID("not-a-valid-uuid");
        assertTrue(result.isEmpty());
    }

    @Test
    void whenUpperCaseNullInput_thenReturnsEmptyOptional() {
        Optional<UUID> result = SafeDataParse.safeParseUUID("NULL");
        assertTrue(result.isEmpty());
    }
}

