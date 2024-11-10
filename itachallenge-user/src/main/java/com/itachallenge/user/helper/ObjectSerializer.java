package com.itachallenge.user.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 15/10/2024 Michel: convierto a métodos por instancia en lugar de estáticos para que se pueda mockear esta clase

    public byte[] serialize(Object obj) throws JsonProcessingException {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot serialize a null object");
        }
        return objectMapper.writeValueAsBytes(obj);
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot deserialize a null byte array");
        }
        return objectMapper.readValue(bytes, clazz);
    }
}
