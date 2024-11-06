package com.itachallenge.score.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectSerializerTest {

    private ObjectSerializer objectSerializer;

    @BeforeEach
    void setUp() {
        objectSerializer = new ObjectSerializer();
    }

    @Test
    void testSerialize() throws JsonProcessingException {

        ScoreRequestDto dto = new ScoreRequestDto();
        dto.setUuidLanguage(UUID.randomUUID());
        dto.setUuidLanguage(UUID.randomUUID());
        dto.setSolutionText("Solution Text Text");

        byte[] result = objectSerializer.serialize(dto);

        assertNotNull(result, "The serialized byte array should not be null.");
        assertTrue(result.length > 0, "The serialized byte array should not be empty.");
    }

    @Test
    void testDeserialize() throws IOException {

        ScoreRequestDto dto = new ScoreRequestDto();
        dto.setUuidChallenge(UUID.randomUUID());
        dto.setUuidLanguage(UUID.randomUUID());
        dto.setSolutionText("Solution Text Test");

        byte[] serialized = objectSerializer.serialize(dto);

        ScoreRequestDto deserializedDto = objectSerializer.deserialize(serialized, ScoreRequestDto.class);

        assertNotNull(deserializedDto, "Deserialized object should not be null.");
        assertEquals(dto.getUuidChallenge(), deserializedDto.getUuidChallenge(), "UUIDs should match");
        assertEquals(dto.getUuidLanguage(), deserializedDto.getUuidLanguage(), "UUIDs should match");
        assertEquals(dto.getSolutionText(), deserializedDto.getSolutionText(), "Solution texts should match");
    }

    @Test
    void testSerializeNullObject() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            objectSerializer.serialize(null);
        });

        assertEquals("Cannot serialize a null object", exception.getMessage());
    }

    @Test
    void testDeserializeNullByteArray() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            objectSerializer.deserialize(null, ScoreRequestDto.class);
        });

        assertEquals("Cannot deserialize a null byte array", exception.getMessage());
    }

    @Test
    void testDeserializeInvalidBytes() {

        byte[] invalidBytes = new byte[]{1, 2, 3, 4, 5};

        Exception exception = assertThrows(IOException.class, () -> {
            objectSerializer.deserialize(invalidBytes, ScoreRequestDto.class);
        });

        assertTrue(exception instanceof IOException);
    }
}
