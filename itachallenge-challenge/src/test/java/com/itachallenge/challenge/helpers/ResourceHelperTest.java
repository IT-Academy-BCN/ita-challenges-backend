package com.itachallenge.challenge.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){

        String expected = "{\r\n" +
                "  \"filterName\": \"RandomName\"\r\n" +
                "}";

        String jsonPath = "json/RandomJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        String result  = null;
        try {
            result = resourceHelper.readResourceAsString();
            //System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected,result);
    }
}
