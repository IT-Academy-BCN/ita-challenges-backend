package com.itachallenge.score.helper;

import com.itachallenge.score.filter.AsciiFilter;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class AsciiFilterTest {


    @Test
    void testFilterValid() {
        AsciiFilter filter = new AsciiFilter();
        assertTrue("The String had a valids ASCII chars", filter.apply(StringTest.ASCIIvalid));
    }

    @Test
    void testFilterInvalid() {
        AsciiFilter filter = new AsciiFilter();
        assertFalse("The String had a invalids ASCII chars", filter.apply(StringTest.ASCIIinvalid));
    }
}
