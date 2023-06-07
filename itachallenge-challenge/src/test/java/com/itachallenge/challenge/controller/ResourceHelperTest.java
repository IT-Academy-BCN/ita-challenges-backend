package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){
        String jsonPath = "RandomJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        String result  = null;
        try {
            result = resourceHelper.readResourceAsString();
            // System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String expected = "{\"name\": \"RandomName\", \"num\": [1,2,3], \"happy\": true}";
        Assertions.assertEquals(expected,result);
    }

    @Test
    void failedReadResourceTest () {
        String invalidPath = "jsonx908erfd/Randosadn90dsmJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(invalidPath);

        Exception exception = assertThrows(IOException.class, () ->
                resourceHelper.readResourceAsString());

        String prefixMsg = "Exception when " + "loading/reading" + " " + invalidPath + " resource: \n";
        assertTrue(exception.getMessage().startsWith(prefixMsg));
    }
}




