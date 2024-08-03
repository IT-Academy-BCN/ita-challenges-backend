package com.itachallenge.score.helper;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class Filter_SimpleValidateTest {

    String ASCIIvalid = """
                  import java.util.Arrays;
                    
            public class Main {
                public static void main(String[] args) {
                    String numbers = "3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray));
                }
            }
                """;

    String ASCIIinvalid = """
                              import java.util.Arrays;
                    
            public class βain {
                public static void main(String[] args) {
                    String numbers = "©, 3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray)); """;


    @Test
    void testFilterValid() {
        SimpleValidate filter = new SimpleValidate(ASCIIvalid);
        assertTrue("The String had a valids ASCII chars", filter.isResult());
    }

    @Test
    void testFilterInvalid() {
        SimpleValidate filter = new SimpleValidate(ASCIIinvalid);
        assertFalse("The String had a invalids ASCII chars", filter.isResult());
    }
}
