package com.itachallenge.challenge.helpers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    void readResourceAsStringTest (){
        String jsonPath = "json/RandomJson.json";
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

    static class RandomDto {
        private String filterName;
        private String[] options;
        private boolean uniqueOption;
        private String[] visibility;

        public RandomDto() {
        }

        public String getFilterName() {
            return filterName;
        }

        public void setFilterName(String filterName) {
            this.filterName = filterName;
        }

        public String[] getOptions() {
            return options;
        }

        public void setOptions(String[] options) {
            this.options = options;
        }

        public boolean isUniqueOption() {
            return uniqueOption;
        }

        public void setUniqueOption(boolean uniqueOption) {
            this.uniqueOption = uniqueOption;
        }

        public String[] getVisibility() {
            return visibility;
        }

        public void setVisibility(String[] visibility) {
            this.visibility = visibility;
        }
    }
}
