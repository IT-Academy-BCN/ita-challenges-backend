package com.itachallenge.score.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
@ExtendWith(MockitoExtension.class)
class JavaFileServiceTest {
    @InjectMocks
    private JavaFileService javaFileService;
    @Mock
    private Logger log;

    private final String storagePath = "/tmp/java-files";
    private final String fileName = "UserSolution.java";

    @BeforeEach
    void setUp() {
        javaFileService = new JavaFileService();
        javaFileService.setStoragePath(storagePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(storagePath, fileName));
    }

    @Test
    void testCreateJavaFile() {
        String userCode = "System.out.println(\"Hello, World!\");";

        try {
            File javaFile = javaFileService.createJavaFile(userCode, fileName);
            assertTrue(javaFile.exists(), "The Java file should be created.");
            assertTrue(javaFile.length() > 0, "The Java file should not be empty.");
        } catch (IOException e) {
            fail("IOException should not be thrown: " + e.getMessage());
        }
    }

    @Test
     void testCreateJavaFileWithInvalidPath() {
        javaFileService.setStoragePath("/invalid/path");

        String userCode = "System.out.println(\"Hello, World!\");";

        try {
            javaFileService.createJavaFile(userCode, fileName);
            fail("IOException should be thrown due to invalid path.");
        } catch (IOException e) {
            assertTrue(e.getMessage().contains("No such file or directory"), "Expected IOException due to invalid path.");
        }
    }
}


