package com.itachallenge.challenge.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ResourceHelper {

    private ObjectMapper mapper;

    private InputStream inputStream;

    private Reader reader;

    public ResourceHelper() {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Works if target class is X.class (or X[].class)
     * Ex of invalid ->
     *      List<X>.class does not compile
     *      List.class compiles, but returns a List<Object>
     *  So, ff the targe is a generic wrapper, USE THE OTHERS METHODS
     *
     * @param resourcePath
     * @param targetClass: mut be X.class (or X[].class).
     * @return
     * @param <T>
     */
    public <T> T mapResource(String resourcePath, Class<T> targetClass){
        try {
            inputStream = new ClassPathResource(resourcePath).getInputStream();
            reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            String json = FileCopyUtils.copyToString(reader);
            return mapper.readValue(json, targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
