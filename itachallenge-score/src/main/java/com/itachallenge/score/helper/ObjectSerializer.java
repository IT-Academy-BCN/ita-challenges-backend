package com.itachallenge.score.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private ObjectMapper objectMapper = new ObjectMapper();


    public byte[] serialize(Object value) throws JsonProcessingException {
        if (value == null) {
            throw new IllegalArgumentException("Cannot serialize a null object");
        }
        return objectMapper.writeValueAsBytes(value);
    }

    public <T> T deserialize(byte[] data, Class<T> valueType) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException("Cannot deserialize a null byte array");
        }
        return objectMapper.readValue(data, valueType);
    }
}
