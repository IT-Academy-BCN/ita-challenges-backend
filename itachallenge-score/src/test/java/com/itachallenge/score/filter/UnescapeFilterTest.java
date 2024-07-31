package com.itachallenge.score.helper;

import com.itachallenge.score.filter.UnescapeFilter;
import com.itachallenge.score.filter.Filter;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

class UnescapeFilterTest {

    @Test
    void testUnescapeValid() {
        UnescapeFilter filter = new UnescapeFilter();
        Filter nextFilter = new MockFilter();
        filter.setNext(nextFilter);

        String escapedInput = "Hello \\u003Cworld\\u003E";
        String expectedOutput = "Hello <world>";
        assertTrue("The unescaped code should be passed to the next filter", filter.apply(escapedInput));
        assertTrue("The unescaped code matches the expected output", MockFilter.lastInput.equals(expectedOutput));
    }

    @Test
    void testNoNextFilter() {
        UnescapeFilter filter = new UnescapeFilter();

        String escapedInput = "Hello \\u003Cworld\\u003E";
        assertTrue("Without next filter, the process should be successful", filter.apply(escapedInput));
    }


    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public boolean apply(String input) {
            lastInput = input;
            return true;
        }

        @Override
        public void setNext(Filter next) {
            // No implementation needed for mock
        }
    }
}
