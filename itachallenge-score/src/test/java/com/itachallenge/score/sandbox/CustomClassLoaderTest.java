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

    @Test
    void testIsLibraryImportAllowed() {
        String codeWithProhibitedImport = "import java.io.File;";
        String codeWithoutProhibitedImport = "import java.util.List;";

        assertFalse(CustomClassLoader.isLibraryImportAllowed(codeWithProhibitedImport), "Code with prohibited import should return false");
        assertTrue(CustomClassLoader.isLibraryImportAllowed(codeWithoutProhibitedImport), "Code without prohibited import should return true");
    }

    @Test
    void testGetProhibitedClassesMethod() {
        CustomClassLoader customClassLoader = new CustomClassLoader(ClassLoader.getSystemClassLoader(), JavaSandboxContainer.getProhibitedClasses());
        List<String> prohibitedClasses = customClassLoader.getProhibitedClasses();

        assertNotNull(prohibitedClasses, "Prohibited classes list should not be null");
        assertTrue(prohibitedClasses.contains("java.io.File"), "Prohibited classes list should contain java.io.File");
        assertTrue(prohibitedClasses.contains("java.io.FileWriter"), "Prohibited classes list should contain java.io.FileWriter");
    }
}