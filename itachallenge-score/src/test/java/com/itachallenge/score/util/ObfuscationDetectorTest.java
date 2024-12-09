package com.itachallenge.score.util;

import org.junit.jupiter.api.Test;

import static com.itachallenge.score.util.ObfuscationDetector.*;
import static org.junit.jupiter.api.Assertions.*;

class ObfuscationDetectorTest {

    @Test
    void testApplyConcatenationWithPlusOperator() {
        String a = "java.i";
        String input = a + "o.File";
        String expected = "java.io.File";
        String result = applyConcatenation(input);

        assertEquals(expected, result, "Concatenation with '+' should be replaced by a single string.");
    }

    @Test
    void testApplyConcatenationWithConcatMethod() {
        String a = "java.i";
        String input = a.concat("o.File");
        String result = applyConcatenation(input);

        assertEquals("java.io.File", result, ".concat() method should be replaced by a single string.");
    }

    @Test
    void testConcatenationOfFourStrings() {
        String a = "jav";
        String b = "o.F";
        String result = applyConcatenation(a + "a.i" + b + "ile");
        assertEquals("java.io.File", result);
    }

}
