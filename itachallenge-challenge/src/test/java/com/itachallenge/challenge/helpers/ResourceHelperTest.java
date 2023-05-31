package com.itachallenge.challenge.helpers;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceHelperTest {

    private String expected;

    @BeforeEach
    void setup(){
        expected = """
                {\r
                  "filterName": "RandomName"\r
                }""";
    }

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){

        String jsonPath = "json/RandomJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        String result  = null;
        try {
            result = resourceHelper.readResourceAsString();
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected,result);
        //Assertions.assertEquals(expected(),result);
    }


    String expected (){
        String resourcePath = "json/RandomJson.json";
        Resource resource = new ClassPathResource(resourcePath);
        String result = null;
        try {
            File file = resource.getFile();
            result = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Assertions.assertEquals(expected,result);
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
