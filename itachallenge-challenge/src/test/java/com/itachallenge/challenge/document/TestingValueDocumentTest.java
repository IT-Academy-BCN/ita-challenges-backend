package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestingValueDocumentTest {

    @Test
    void testGetAndSetInParam() {
        // Arrange
        List<?> inParam = Arrays.asList("input1", "input2");
        TestingValueDocument document = new TestingValueDocument();

        // Act
        document.setInParam(inParam);

        // Assert
        assertEquals(inParam, document.getInParam());
    }

    @Test
    void testGetAndSetOutParam() {
        // Arrange
        List<?> outParam = Arrays.asList("output1", "output2");
        TestingValueDocument document = new TestingValueDocument();

        // Act
        document.setOutParam(outParam);

        // Assert
        assertEquals(outParam, document.getOutParam());
    }
}
