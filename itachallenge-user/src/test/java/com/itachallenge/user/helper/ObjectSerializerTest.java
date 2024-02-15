package com.itachallenge.user.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ObjectSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectSerializer objectSerializer = new ObjectSerializer();

    @Test
    void serialize() throws JsonProcessingException {
        String testObject = "test";
        byte[] expected = objectMapper.writeValueAsBytes(testObject);
        byte[] actual = objectSerializer.serialize(testObject);

        assertArrayEquals(expected, actual);
    }

    @Test
    void deserialize() throws IOException {
        String testObject = "test";
        byte[] serializedTestObject = objectMapper.writeValueAsBytes(testObject);

        String actual = objectSerializer.deserialize(serializedTestObject, String.class);

        assertEquals(testObject, actual);
    }
}