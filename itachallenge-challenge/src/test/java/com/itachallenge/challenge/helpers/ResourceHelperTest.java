package com.itachallenge.challenge.helpers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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

        String jsonPath = "json/RandomJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        String result;
        try {
            result = resourceHelper.readResourceAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(expected,result);
    }

    @Test
    @DisplayName("Map a resource to an object test")
    void mapResourceToObjectTest(){
        RandomDto expected = new RandomDto();
        expected.setFilterName("RandomName");
        expected.setOptions(new String[]{"Option1","Option2","Option3"});
        expected.setUniqueOption(true);
        expected.setVisibility(new String[]{"ROLE_X","ROLE_Y"});

        String jsonPath = "json/RandomJson.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        RandomDto result;
        try {
            result = resourceHelper.mapResourceToObject(RandomDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(result).usingRecursiveComparison().isEqualTo(expected).withStrictTypeChecking();
    }

    @NoArgsConstructor
    @Getter
    @Setter
    static class RandomDto {
        private String filterName;
        private String[] options;
        private boolean uniqueOption;
        private String[] visibility;
    }
}
