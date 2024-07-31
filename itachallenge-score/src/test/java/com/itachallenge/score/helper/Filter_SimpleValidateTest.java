package com.itachallenge.score.helper;

import com.itachallenge.score.filter.StringTest;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class Filter_SimpleValidateTest {


    @Test
    void testFilterValid() {
        SimpleValidate filter = new SimpleValidate(StringTest.ASCIIvalid);
        assertTrue("The String had a valids ASCII chars", filter.isResult());
    }

    @Test
    void testFilterInvalid() {
        SimpleValidate filter = new SimpleValidate(StringTest.ASCIIinvalid);
        assertFalse("The String had a invalids ASCII chars", filter.isResult());
    }
}
