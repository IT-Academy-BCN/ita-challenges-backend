package com.itachallenge.score.util;

import com.itachallenge.score.exception.FileProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileUtilTest {

    private final FileUtil fileUtil = new FileUtil();

    @Test
    void testCreateTestParamsFile_Success(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        List<String> testParams = List.of("key1=value1", "key2=value2");;

        UUID solutionId = UUID.randomUUID();

        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists());

        String content = Files.readString(createdFile.toPath());
        assertTrue(content.contains("key1=value1"));
        assertTrue(content.contains("key2=value2"));
    }

    @Test
    void testCreateTestParamsFile_EmptyParams(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        List<String> testParams = List.of();
        UUID solutionId = UUID.randomUUID();

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(testParams, solutionId);
        }, "An exception should be thrown for empty parameters");
    }

    @Test
    void testCreateTestParamsFile_NullParams(@TempDir Path tempDir) {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        UUID solutionId = UUID.randomUUID();

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(null, solutionId);
        }, "An exception should be thrown for null parameters");
    }

    @Test
    void testCreateTestParamsFile_NullSolutionId(@TempDir Path tempDir) {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        List<String> testParams = List.of("key1=value1");

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(testParams, null);
        }, "An exception should be thrown for null solution ID");
    }

    @Test
    void testCreateTestParamsFile_LargeNumberOfParams(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        List<String> testParams = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            testParams.add("key" + i + "=value" + i);
        }

        UUID solutionId = UUID.randomUUID();

        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists(), "The file should be created");

        String content = Files.readString(createdFile.toPath());
        for (int i = 0; i < 10000; i++) {
            assertTrue(content.contains("key" + i + "=value" + i),
                    "The file should contain 'key" + i + "=value" + i + "'");
        }
    }
}
