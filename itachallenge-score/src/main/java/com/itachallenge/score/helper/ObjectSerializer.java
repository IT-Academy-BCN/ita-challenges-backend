package com.itachallenge.score.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private final ObjectMapper objectMapper;

    public ObjectSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public byte[] serialize(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(value);
    }

    public <T> T deserialize(byte[] data, Class<T> valueType) throws IOException {
        return objectMapper.readValue(data, valueType);
    }
}
