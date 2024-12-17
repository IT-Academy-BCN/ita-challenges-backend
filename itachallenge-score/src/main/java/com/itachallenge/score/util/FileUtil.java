package com.itachallenge.score.util;
import com.itachallenge.score.exception.FileProcessingException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${sandbox.input-dir}")
    private String inputDir;

    public void createTestParamsFile(List<String> testParams, UUID solutionId)  {

        if (testParams == null || testParams.isEmpty()) {
            throw new FileProcessingException("testParams cannot be null or empty");
        }
        if (solutionId == null) {
            throw new FileProcessingException("solutionId cannot be null");
        }
        try {
            File directory = new File(inputDir);
            if (!directory.exists()) {
                FileUtils.forceMkdir(directory);

            }
            String fileName = "parameters_" + solutionId + ".txt";
            String filePath = Paths.get(inputDir, fileName).toString();
            File file = new File(filePath);

            FileUtils.writeStringToFile(file, String.join("\n", testParams), StandardCharsets.UTF_8);
            } catch (IOException e) {
            throw new FileProcessingException("Failed to create test parameters file", e);
        }
    }
}