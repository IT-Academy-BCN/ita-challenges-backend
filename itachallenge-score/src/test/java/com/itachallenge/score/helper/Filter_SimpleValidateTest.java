package com.itachallenge.score.helper;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Filter_SimpleValidateTest {


    @Test
    public void testFilterValid() {
        SimpleValidate filter = new SimpleValidate(StringTest.ASCIIvalid);
        assertTrue("The String had a valids ASCII chars", filter.isResult());
    }

    @Test
    public void testFilterInvalid() {
        SimpleValidate filter = new SimpleValidate(StringTest.ASCIIinvalid);
        assertFalse("The String had a invalids ASCII chars", filter.isResult());
    }
}
