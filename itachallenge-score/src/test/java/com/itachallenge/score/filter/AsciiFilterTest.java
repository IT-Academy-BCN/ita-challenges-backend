package com.itachallenge.score.filter;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class AsciiFilterTest {

    @Test
    void testFilterValid() {
        AsciiFilter filter = new AsciiFilter();
        assertTrue("The String had a valid ASCII chars", filter.apply(StringTest.ASCIIvalid));
    }

    @Test
    void testFilterInvalid() {
        AsciiFilter filter = new AsciiFilter();
        assertFalse("The String had invalid ASCII chars", filter.apply(StringTest.ASCIIinvalid));
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
