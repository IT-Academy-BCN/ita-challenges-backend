package com.itachallenge.challenge.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Objects;

public class ObjectSerializerTest {

    @Test
    void serializeAndDeserialize() throws IOException {
        // Arrange
        TestObject originalObject = new TestObject("Test", 123);
        ObjectSerializer objectSerializer = new ObjectSerializer();

        // Act
        byte[] serializedObject = objectSerializer.serialize(originalObject);
        TestObject deserializedObject = objectSerializer.deserialize(serializedObject, TestObject.class);

        // Assert
        assertEquals(originalObject, deserializedObject);
    }

    public static class TestObject {
        private String name;
        private int value;

        // getters, setters, equals, and hashCode methods...

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public TestObject() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObject that = (TestObject) o;
            return value == that.value && name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }
}
