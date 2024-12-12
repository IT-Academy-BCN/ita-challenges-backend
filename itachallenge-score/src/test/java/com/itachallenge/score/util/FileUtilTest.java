package com.itachallenge.score.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
public class FileUtilTest {

    private final FileUtil fileUtil = new FileUtil();

    @Test
    void testCreateTestParamsFile_Success(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        Map<String, Object> testParams = new HashMap<>();
        testParams.put("key1", "value1");
        testParams.put("key2", "value2");
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

        Map<String, Object> testParams = new HashMap<>();
        UUID solutionId = UUID.randomUUID();

        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists());

        String content = Files.readString(createdFile.toPath());
        assertTrue(content.isEmpty() || content.equals("\n"));
    }
    @Test
    void testCreateTestParamsFile_NullParams(@TempDir Path tempDir) {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        UUID solutionId = UUID.randomUUID();

        assertThrows(IllegalArgumentException.class, () -> {
            fileUtil.createTestParamsFile(null, solutionId);
        });
    }
    @Test
    void testCreateTestParamsFile_NullSolutionId(@TempDir Path tempDir) {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        Map<String, Object> testParams = new HashMap<>();

        assertThrows(IllegalArgumentException.class, () -> {
            fileUtil.createTestParamsFile(testParams, null);
        });
    }
    @Test
    void testCreateTestParamsFile_LargeNumberOfParams(@TempDir Path tempDir) throws Exception {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());

        Map<String, Object> testParams = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            testParams.put("key" + i, "value" + i);
        }

        UUID solutionId = UUID.randomUUID();

        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists());

        String content = Files.readString(createdFile.toPath());

        for (int i = 0; i < 10000; i++) {
            assertTrue(content.contains("key" + i + "=value" + i));
        }
    }
}
