package com.itachallenge.score.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public byte[] serialize(Object obj) throws JsonProcessingException {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize a null object");
        }
        return objectMapper.writeValueAsBytes(obj);
    }

    public <T> T deserialize(byte[] bytes, Class<T> valueType) throws IOException {
        if (bytes == null) {
            throw new IllegalArgumentException("Cannot deserialize a null byte array");
        }
        if (valueType == null) {
            throw new IllegalArgumentException("Cannot deserialize to a null class type");
        }
        return objectMapper.readValue(bytes, valueType);
    }
}
