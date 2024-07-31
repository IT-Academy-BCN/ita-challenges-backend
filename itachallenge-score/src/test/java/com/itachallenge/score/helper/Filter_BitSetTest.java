package com.itachallenge.score.helper;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class Filter_BitSetTest {


    @Test
    void testFilterValid() {
        FilterBitSet filter = new FilterBitSet(StringTest.ASCIIvalid);
        assertTrue("The String had a valids ASCII chars", filter.isResult());
    }

    @Test
    void testFilterInvalid() {
        FilterBitSet filter = new FilterBitSet(StringTest.ASCIIinvalid);
        assertFalse("The String had a invalids ASCII chars", filter.isResult());
    }
}
