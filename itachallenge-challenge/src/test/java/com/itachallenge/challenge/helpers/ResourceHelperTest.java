package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.helper.ResourceHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){
        ResourceHelper resourceHelper = new ResourceHelper("json/random.json");
        String expected = "{\"name\": \"RandomName\", \"num\": [1,2,3], \"happy\": true}";
        assertEquals(expected, resourceHelper.readResourceAsString().get());
    }

    @Test
    void failedReadResourceTest () {
        String invalidPath = "jsonx908erfd/Randosadn90dsmJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(invalidPath);
        assertEquals(null, resourceHelper.readResourceAsString());
    }
}