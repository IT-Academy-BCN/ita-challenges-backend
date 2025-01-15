package com.itachallenge.score.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.itachallenge.score.util.ObfuscationDetector.*;
import static org.junit.jupiter.api.Assertions.*;

class ObfuscationDetectorTest {

    static Stream<Arguments> applyConcatenationTestCases() {
        return Stream.of(
                Arguments.of("Some code \"java.io.File\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"java.i\" + \"o.File\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"jav\" + \"a.i\" + \"o.Fi\" + \"le\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"java.i\".concat(\"o.File\") Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"jav\".concat(\"a.i\").concat(\"o.F\").concat(\"ile\") Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"jav\" + \"a.i\".concat(\"o.Fi\") + \"le\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"jav\".concat(\"a.i\") + \"o.F\" + \"il\".concat(\"e\") Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"java.i\" +  \"o.File\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"java.i\"+\"o.File\" Some code",
                        "Some code \"java.io.File\" Some code"),
                Arguments.of("Some code \"java.i\" + \"o.\" + \"File\" Some code",
                        "Some code \"java.io.File\" Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applyConcatenationTestCases")
    void applyConcatenationTest(String input, String expected) {
        String result = applyConcatenation(input);
        assertEquals(expected, result, "Concatenation should be replaced by a single string.");
    }

    static Stream<Arguments> applySubstringTestCases() {
        return Stream.of(
                Arguments.of("Some code interface Some code",
                        "Some code interface Some code"),
                Arguments.of("Some code pinterface6.substring(1, 10) Some code",
                        "Some code interface Some code"),
                Arguments.of("Some code aString + pinterface6.substring(1, 10) Some code",
                        "Some code aString + interface Some code"),
                Arguments.of("Some code pinterface6pointer.substring(1, 10).substring(0, 9) Some code",
                        "Some code interface Some code"),
                Arguments.of("Some code ppppinterface6pointers.substring(1, 17).substring(2, 15).substring(1,10) Some code",
                        "Some code interface Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applySubstringTestCases")
    void applySubstringTest(String input, String expected) {
        String result = applySubstring(input);
        assertEquals(expected, result, "Converts String to lower case");
    }

    static Stream<Arguments> applyCharacterToUpperOrLowerCaseTestCases() {
        return Stream.of(
                Arguments.of("Some code Character.toUpperCase('a') Some code", "Some code 'A' Some code"),
                Arguments.of("Some code Character.toLowerCase('A') Some code", "Some code 'a' Some code"),
                Arguments.of("Some code Character.toUpperCase('1') Some code", "Some code '1' Some code"),
                Arguments.of("Some code Character.toUpperCase('é') Some code", "Some code 'É' Some code"),
                Arguments.of("Some code Character.toLowerCase('É') Some code", "Some code 'é' Some code"),
                Arguments.of("Some code Character.toUpperCase(' ') Some code", "Some code ' ' Some code"),
                Arguments.of("Some code Character.toUpperCase('a') Character.toLowerCase('B') Some code",
                        "Some code 'A' 'b' Some code"),
                Arguments.of("Some code Character.toUpperCase('a').toLowerCase() Character.toLowerCase('B') Some code",
                        "Some code 'A'.toLowerCase() 'b' Some code"),
                Arguments.of("Some code Character.toUpperCase('Z') Character.toLowerCase('E') Some code",
                        "Some code 'Z' 'e' Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applyCharacterToUpperOrLowerCaseTestCases")
    void applyCharacterToUpperOrLowerCaseTest(String input, String expected) {
        String result = applyCharacterToUpperOrLowerCase(input);
        assertEquals(expected, result, "Converts Character to lower or upper case");
    }

    static Stream<Arguments> applyToUpperOrLowerCaseTestCases() {
        return Stream.of(
                Arguments.of("Some code \"a\".toUpperCase() Some code",
                        "Some code \"A\" Some code"),
                Arguments.of("Some code \"A\".toLowerCase() Some code",
                        "Some code \"a\" Some code"),
                Arguments.of("Some code \"class\".toUpperCase() Some code",
                        "Some code \"CLASS\" Some code"),
                Arguments.of("Some code \"class\".toUpperCase().toLowerCase().toUpperCase() Some code",
                        "Some code \"CLASS\" Some code"),
                Arguments.of("Some code \"CLASS\".toLowerCase() Some code",
                        "Some code \"class\" Some code"),
                Arguments.of("Some code \"heLLo123wORLd\".toUpperCase() Some code",
                        "Some code \"HELLO123WORLD\" Some code"),
                Arguments.of("Some code \"heLLo123wORLd\".toLowerCase() Some code",
                        "Some code \"hello123world\" Some code"),
                Arguments.of("Some code \"java.io.File\".toUpperCase() Some code",
                        "Some code \"JAVA.IO.FILE\" Some code"),
                Arguments.of("Some code \"JAVA.IO.FILE\".toLowerCase() Some code",
                        "Some code \"java.io.file\" Some code"),
                Arguments.of("Some code \"@camión$\".toUpperCase() Some code",
                        "Some code \"@CAMIÓN$\" Some code"),
                Arguments.of("Some code \"@CAMIÓN$\".toLowerCase() Some code",
                        "Some code \"@camión$\" Some code"),
                Arguments.of("Some code \"interafade\".replace('d', 'c')",
                        "Some code \"interafade\".replace('d', 'c')")
        );
    }
    @ParameterizedTest
    @MethodSource("applyToUpperOrLowerCaseTestCases")
    void applyToUpperOrLowerCaseTest(String input, String expected) {
        String result = applyToUpperOrLowerCase(input);
        assertEquals(expected, result, "Converts String to upper case");
    }

    static Stream<Arguments> applyReplaceAndReplaceAllCases() {
        return Stream.of(
                Arguments.of("Some code \"class\".replace('z', 'a') Some code",
                        "Some code \"class\" Some code"),
                Arguments.of("Some code \"clapp\".replace('p', 's') Some code",
                        "Some code \"class\" Some code"),
                Arguments.of("Some code \"classo\".replace('o', '') Some code",
                        "Some code \"class\" Some code"),
                Arguments.of("Some code \"impxrt java.ix.File\".replace('x', 'o') Some code",
                        "Some code \"import java.io.File\" Some code"),
                Arguments.of("Some code \"import javo.oo.oile\".replace(\"o.oo.o\", \"a.io.F\") Some code",
                        "Some code \"import java.io.File\" Some code"),
                Arguments.of("Some code \"intwentyace\".replace(\"wenty\", \"erf\") Some code",
                        "Some code \"interface\" Some code"),
                Arguments.of("Some code \"interthirtydog\".replace(\"thirty\", \"\").replace(\"dog\", \"face\") Some code",
                        "Some code \"interface\" Some code"),
                Arguments.of("Some code \"ixport javy.io.Sile\".replace('x', 'm').replace('y', 'a').replace('S', 'F') Some code",
                        "Some code \"import java.io.File\" Some code"),
                Arguments.of("Some code \"clapp\".replaceAll('p', 's') Some code",
                        "Some code \"class\" Some code"),
                Arguments.of("Some code \"impxrt java.ix.File\".replaceAll('x', 'o') Some code",
                        "Some code \"import java.io.File\" Some code"),
                Arguments.of("Some code \"import javo.oo.oile\".replaceAll(\"o.oo.o\", \"a.io.F\") Some code",
                        "Some code \"import java.io.File\" Some code"),
                Arguments.of("Some code \"intwentyace\".replaceAll(\"wenty\", \"erf\") Some code",
                        "Some code \"interface\" Some code"),
                Arguments.of("Some code \"interthirtydog\".replaceAll(\"thirty\", \"\")" +
                                ".replaceAll(\"dog\", \"face\") Some code",
                        "Some code \"interface\" Some code"),
                Arguments.of("Some code \"ixport javy.io.Sile\".replaceAll('x', 'm')" +
                                ".replaceAll('y', 'a').replaceAll('S', 'F') Some code",
                        "Some code \"import java.io.File\" Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applyReplaceAndReplaceAllCases")
    void applyReplaceStringLiteralsTest(String input, String expected) {
        String result = applyReplaceStringLiterals(input);
        assertEquals(expected, result);
    }

}
