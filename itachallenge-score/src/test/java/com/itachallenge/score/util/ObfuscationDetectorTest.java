package com.itachallenge.score.util;

import org.junit.jupiter.api.Test;
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

    static Stream<Arguments> applyToUpperCaseTestCases() {
        return Stream.of(
                Arguments.of("\"a\".toUpperCase())", "\"A\")"),
                Arguments.of("Some code \"class\".toUpperCase() Some code ",
                        "Some code \"CLASS\" Some code "),
                Arguments.of("Some code \"CLASS\".toUpperCase() Some code ",
                        "Some code \"CLASS\" Some code "),
                Arguments.of("Some code \"heLLo123wORLd\".toUpperCase() Some code ",
                        "Some code \"HELLO123WORLD\" Some code "),
                Arguments.of("Some code \"java.io.File\".toUpperCase() Some code ",
                        "Some code \"JAVA.IO.FILE\" Some code "),
                Arguments.of("Some code \"camión\".toUpperCase() Some code ",
                        "Some code \"CAMIÓN\" Some code "),
                Arguments.of("Some code \"@class$\".toUpperCase() Some code ",
                        "Some code \"@CLASS$\" Some code "),
                Arguments.of("Some code \"CLASS\".toLowerCase() Some code ",
                        "Some code \"CLASS\".toLowerCase() Some code ")
        );
    }
    @ParameterizedTest
    @MethodSource("applyToUpperCaseTestCases")
    void applyToUpperCaseTest(String input, String expected) {
        String result = applyToUpperCase(input);
        assertEquals(expected, result, "Coverts String to upper case");
    }

    static Stream<Arguments> applyToLowerCaseTestCases() {
        return Stream.of(
                Arguments.of("Some code \"A\".toLowerCase() Some code ",
                        "Some code \"a\" Some code "),
                Arguments.of("Some code \"CLASS\".toLowerCase() Some code ",
                        "Some code \"class\" Some code "),
                Arguments.of("Some code \"class\".toLowerCase() Some code ",
                        "Some code \"class\")"),
                Arguments.of("Some code \"heLLo123wORLd\".toLowerCase() Some code ",
                        "Some code \"hello123world\")"),
                Arguments.of("Some code \"JAVA.IO.FILE\".toLowerCase() Some code ",
                        "Some code \"java.io.file\")"),
                Arguments.of("Some code \"CAMIÓN\".toLowerCase() Some code ",
                        "Some code \"camión\")"),
                Arguments.of("Some code \"@CLASS$\".toLowerCase() Some code ",
                        "Some code \"@class$\")"),
                Arguments.of("Some code \"class\".toUpperCase() Some code ",
                        "Some code \"class\".toUpperCase())")
        );
    }
    @ParameterizedTest
    @MethodSource("applyToLowerCaseTestCases")
    void applyToLowerCaseTest(String input, String expected) {
        String result = applyToLowerCase(input);
        assertEquals(expected, result, "Coverts String to lower case");
    }

    static Stream<Arguments> applyCharacterToUpperOrLowerCaseTestCases() {
        return Stream.of(
                Arguments.of("Some code Character.toUpperCase('a') Some code", "Some code A Some code"),
                Arguments.of("Some code Character.toLowerCase('A') Some code", "Some code a Some code"),
                Arguments.of("Some code Character.toUpperCase('1') Some code", "Some code 1 Some code"),
                Arguments.of("Some code Character.toUpperCase('é') Some code", "Some code É Some code"),
                Arguments.of("Some code Character.toLowerCase('É') Some code", "Some code é Some code"),
                Arguments.of("Some code Character.toUpperCase(' ') Some code", "Some code   Some code"),
                Arguments.of("Some code Character.toUpperCase('a') Character.toLowerCase('B') Some code",
                        "Some code A b Some code"),
                Arguments.of("Some code Character.toUpperCase('Z') Character.toLowerCase('E') Some code",
                        "Some code Z e Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applyCharacterToUpperOrLowerCaseTestCases")
    void applyCharacterToUpperOrLowerCaseTest(String input, String expected) {
        String result = applyCharacterToUpperOrLowerCase(input);
        assertEquals(expected, result, "Converts String to lower case");
    }

    static Stream<Arguments> applySubstringTestCases() {
        return Stream.of(
                Arguments.of("Some code pinterface6.substring(1, 10) Some code",
                        "Some code interface Some code"),
                Arguments.of("Some code pinterface6pointer.substring(1, 10).substring(0, 9) Some code",
                        "Some code interface Some code"),
                Arguments.of("Some code pinterface6 + anotherString.substring(2, 8) Some code",
                        "Some code pinterface6 + otherS Some code")
        );
    }
    @ParameterizedTest
    @MethodSource("applySubstringTestCases")
    void applySubstringTest(String input, String expected) {
        String result = applySubstring(input);
        assertEquals(expected, result, "Converts String to lower case");
    }

//    @ParameterizedTest
//    @MethodSource("provideLiteralReplaceStrings")
//    void applyReplaceStringLiteralsTest(String input, String expected) {
//        String result = applyReplaceStringLiterals(input);
//        assertEquals(expected, result);
//    }
//
//    static Stream<Arguments> provideLiteralReplaceStrings() {
//        return Stream.of(
//                Arguments.of("\"clapp.replace('p', 'a')\"", "\"class\""),
//                Arguments.of("\"class.replace('z', 'a')\"", "\"class\""),
//                Arguments.of("\"classo.replace('o', '')\"", "\"class\""),
//                Arguments.of("\"impxrt java.ix.File.replace('x', 'o')\"", "\"import java.io.File\""),
//                Arguments.of("\"import javo.oo.oile.replace('o.oo.o', 'a.io.F')\"", "\"import java.io.File\""),
//                Arguments.of("\"ixport javy.io.Sile.replace('x', 'm').replace('y', 'a').replace('S', 'F')\"", "\"import java.io.File\"")
//        );
//    }


}
