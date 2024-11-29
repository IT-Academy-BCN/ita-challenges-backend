package com.itachallenge.score.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JavaFileServiceTest {

    @Autowired
    private JavaFileService javaFileService;

    @TempDir
    Path tempDir;

    @Test
    void testCreateJavaFile() throws IOException {
        // Arrange
        String userCode = "System.out.println(\"Hello, World!\");";
        String fileName = "UserSolution.java";
        javaFileService.setStoragePath(tempDir.toString());

        // Act
        File javaFile = javaFileService.createJavaFile(userCode, fileName);

        // Assert
        String fileContent = Files.readString(javaFile.toPath(), StandardCharsets.UTF_8);

        assertTrue(fileContent.contains("System.out.println(\"Hello, World!\");"));
        assertTrue(fileContent.contains("import java.util.List;"));
        assertTrue(fileContent.contains("import java.util.ArrayList;"));
        assertTrue(fileContent.contains("public class UserSolution {"));
        assertTrue(fileContent.contains("public static void main(String[] args) {"));
        assertTrue(fileContent.contains("try {"));
        assertTrue(fileContent.contains("} catch (Exception e) {"));
        assertTrue(fileContent.contains("System.err.println(\"Error executing user code:\");"));
        assertTrue(fileContent.contains("e.printStackTrace();"));
    }
}
