package com.itachallenge.score.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private ObjectMapper objectMapper;

    public ObjectSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public byte[] serialize(Object obj) throws JsonProcessingException {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize a null object");
        }
        return objectMapper.writeValueAsBytes(obj);
    }

    public <T> T deserialize(byte[] src, Class<T> valueType) throws IOException {
        if (src == null) {
            throw new IllegalArgumentException("Cannot deserialize a null byte array");
        }
        return objectMapper.readValue(src, valueType);
    }
}
