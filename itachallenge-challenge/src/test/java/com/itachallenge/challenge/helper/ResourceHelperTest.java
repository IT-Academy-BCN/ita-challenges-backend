package com.itachallenge.challenge.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){

        String expected = "{\r\n" +
                "  \"filterName\": \"RandomName\",\r\n" +
                "  \"options\": [\r\n" +
                "    \"Option1\",\r\n" +
                "    \"Option2\",\r\n" +
                "    \"Option3\"\r\n" +
                "  ],\r\n" +
                "  \"uniqueOption\": true,\r\n" +
                "  \"visibility\": [\r\n" +
                "    \"ROLE_X\",\r\n" +
                "    \"ROLE_Y\"\r\n" +
                "  ]\r\n" +
                "}";

        ResourceHelper resourceHelper = new ResourceHelper();
        String jsonPath = "json/RandomJson.json";
        String result  = null;
        try {
            result = resourceHelper.readResourceAsString(jsonPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected,result);
    }
}
