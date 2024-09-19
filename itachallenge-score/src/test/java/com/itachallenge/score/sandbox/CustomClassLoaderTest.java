package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CustomClassLoaderTest {

    @Test
    void testCustomClassLoaderBlocksProhibitedClasses() {
        List<String> prohibitedClasses = JavaSandboxContainer.getProhibitedClasses();
        CustomClassLoader customClassLoader = new CustomClassLoader(ClassLoader.getSystemClassLoader(), prohibitedClasses);

        for (String prohibitedClass : prohibitedClasses) {
            assertThrows(ClassNotFoundException.class, () -> {
                customClassLoader.loadClass(prohibitedClass);
            }, "Class " + prohibitedClass + " should be prohibited");
        }
    }

    @Test
    void testGetProhibitedClasses() {
        List<String> prohibitedClasses = JavaSandboxContainer.getProhibitedClasses();
        assertNotNull(prohibitedClasses, "Prohibited classes list should not be null");
        assertTrue(prohibitedClasses.contains("java.io.File"), "Prohibited classes list should contain java.io.File");
        assertTrue(prohibitedClasses.contains("java.io.FileWriter"), "Prohibited classes list should contain java.io.FileWriter");
    }
}