package com.itachallenge.score.helper;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Filter_BitSetTest {


    @Test
    public void testFilterValid() {
        Filter_BitSet filter = new Filter_BitSet(StringTest.ASCIIvalid);
        assertTrue("The String had a valids ASCII chars", filter.isResult());
    }

    @Test
    public void testFilterInvalid() {
        Filter_BitSet filter = new Filter_BitSet(StringTest.ASCIIinvalid);
        assertFalse("The String had a invalids ASCII chars", filter.isResult());
    }
}
