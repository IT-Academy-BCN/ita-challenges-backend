package com.itachallenge.score.util;

import com.itachallenge.score.exception.FileProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest {

    private final FileUtil fileUtil = new FileUtil();

    private void setInputDir(FileUtil fileUtil, Path tempDir) {
        ReflectionTestUtils.setField(fileUtil, "inputDir", tempDir.toString());
    }

    @Test
    void testCreateTestParamsFile_Success(@TempDir Path tempDir) throws Exception {
        setInputDir(fileUtil, tempDir);

        List<String> testParams = List.of(
                "\"ABCBDAB\",\"BDCAB\"=\"BCAB\"",
                "\"abcdef\",\"acf\"=\"acf\""
        );
        UUID solutionId = UUID.randomUUID();

        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists(),"The file should be created");

        String content = Files.readString(createdFile.toPath());
        assertTrue(content.contains("\"ABCBDAB\",\"BDCAB\"=\"BCAB\""));
        assertTrue(content.contains("\"abcdef\",\"acf\"=\"acf\""));
    }

    @Test
    void testCreateTestParamsFile_EmptyParams(@TempDir Path tempDir) throws Exception {
        setInputDir(fileUtil, tempDir);

        List<String> testParams = List.of();
        UUID solutionId = UUID.randomUUID();

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(testParams, solutionId);
        }, "An exception should be thrown for empty parameters");
    }

    @Test
    void testCreateTestParamsFile_NullParams(@TempDir Path tempDir) {
        setInputDir(fileUtil, tempDir);

        UUID solutionId = UUID.randomUUID();

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(null, solutionId);
        }, "An exception should be thrown for null parameters");
    }

    @Test
    void testCreateTestParamsFile_NullSolutionId(@TempDir Path tempDir) {
        setInputDir(fileUtil, tempDir);

        List<String> testParams = List.of("key1=value1");

        assertThrows(FileProcessingException.class, () -> {
            fileUtil.createTestParamsFile(testParams, null);
        }, "An exception should be thrown for null solution ID");
    }

    @Test
    void testCreateTestParamsFile_LargeNumberOfParams(@TempDir Path tempDir) throws Exception {
        setInputDir(fileUtil, tempDir);

        List<String> testParams = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            String inParam = "\"key" + i + "\",\"value" + i + "\"";
            String outParam = "\"result" + i + "\"";
            testParams.add(inParam + "=" + outParam);
        }
        UUID solutionId = UUID.randomUUID();
        fileUtil.createTestParamsFile(testParams, solutionId);

        File createdFile = new File(tempDir.toFile(), "parameters_" + solutionId + ".txt");
        assertTrue(createdFile.exists(), "The file should be created");

        List<String> lines = Files.readAllLines(createdFile.toPath());
        assertEquals(testParams.size(), lines.size(), "The number of lines should match");

        for (int i = 0; i < testParams.size(); i++) {
            assertEquals(testParams.get(i), lines.get(i), "Each line should match the corresponding parameter");
    }
}
}
