package com.itachallenge.user.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ObjectSerializer {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static byte[] serialize(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        return objectMapper.readValue(bytes, clazz);
    }
}
