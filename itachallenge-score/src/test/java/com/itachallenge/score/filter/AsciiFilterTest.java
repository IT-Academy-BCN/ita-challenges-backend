package com.itachallenge.score.filter;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class AsciiFilterTest {

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
        AsciiFilter filter = new AsciiFilter();
        assertTrue("The String had a valid ASCII chars", filter.apply(ASCIIvalid));
    }

    @Test
    void testFilterInvalid() {
        AsciiFilter filter = new AsciiFilter();
        assertFalse("The String had invalid ASCII chars", filter.apply(ASCIIinvalid));
    }

    @Test
    void testEmptyString() {
        AsciiFilter filter = new AsciiFilter();
        assertTrue("Empty string should be valid", filter.apply(""));
    }

    @Test
    void testBoundaryCharacters() {
        AsciiFilter filter = new AsciiFilter();
        assertTrue("Boundary characters should be valid", filter.apply("\u0000\u007F"));
    }


    @Test
    void testLongString() {
        AsciiFilter filter = new AsciiFilter();
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            longString.append('a');
        }
        assertTrue("Long string with valid ASCII chars should be valid", filter.apply(longString.toString()));
    }
}
