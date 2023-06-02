package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.dtos.testing.AssociatedRandomDto;
import com.itachallenge.challenge.dtos.testing.RandomDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
    @DisplayName("Failing read a resource test")
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
        AssociatedRandomDto associatedAlone = new AssociatedRandomDto();
        associatedAlone.setTitle("title1");
        associatedAlone.setValues(List.of(1.1f,1.2f));

        AssociatedRandomDto asociatedFirst = new AssociatedRandomDto();
        asociatedFirst.setTitle("title2");
        asociatedFirst.setValues(List.of(2.1f,2.2f));

        AssociatedRandomDto asociatedSecond = new AssociatedRandomDto();
        asociatedSecond.setTitle("title3");
        asociatedSecond.setValues(List.of(3.1f,3.2f));


        RandomDto expected = new RandomDto();
        expected.setName("RandomName");
        expected.setValues(new int[]{1,2,3});
        expected.setHappy(true);
        expected.setOther(associatedAlone);
        expected.setOthers(Set.of(asociatedFirst,asociatedSecond));

        String jsonPath = "json/RandomJson2.json";
        ResourceHelper resourceHelper = new ResourceHelper(jsonPath);
        RandomDto result;
        try {
            result = resourceHelper.mapResourceToObject(RandomDto.class);
            //System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

}
