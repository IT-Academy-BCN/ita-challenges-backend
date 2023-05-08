package com.itachallenge.challenge.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ResourceHelperTest {

    @Test
    @DisplayName("Read a resource as String test")
    public void readResourceAsStringTest (){
        /*
        //String copied from RandomJson.json (LF format)
        //-> test fails.
        //does not matter if IDE is set in LF, CRLF, or CR
        //always, on comparison failure: expected is LF and actual is CRLF
        String expected = "{\n" +
                "  \"filterName\": \"RandomName\",\n" +
                "  \"options\": [\n" +
                "    \"Option1\",\n" +
                "    \"Option2\",\n" +
                "    \"Option3\"\n" +
                "  ],\n" +
                "  \"uniqueOption\": true,\n" +
                "  \"visibility\": [\n" +
                "    \"ROLE_X\",\n" +
                "    \"ROLE_Y\"\n" +
                "  ]\n" +
                "}";
         */

        /*
        //replacing \n for \n (CR format)
        //-> test fails.
        //does not matter if IDE is set in LF, CRLF, or CR
        //always, on comparison failure: expected is CR and actual is CRLF
        String expected = "{\r" +
                "  \"filterName\": \"RandomName\",\r" +
                "  \"options\": [\r" +
                "    \"Option1\",\r" +
                "    \"Option2\",\r" +
                "    \"Option3\"\r" +
                "  ],\r" +
                "  \"uniqueOption\": true,\r" +
                "  \"visibility\": [\r" +
                "    \"ROLE_X\",\r" +
                "    \"ROLE_Y\"\r" +
                "  ]\r" +
                "}";
         */


        //Adding \r before \n (as in previous PR) (CRLF format)
        //test succeeds (in any IDE option: LF, CRLF, or CR)
        //expected CRLF and actual CRLF
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

        //Conclusion: ResourceHelper result(actual) is always CRLF
        //my git config has been always core.autocrlf=true



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
