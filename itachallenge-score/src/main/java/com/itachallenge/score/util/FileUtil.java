package com.itachallenge.score.util;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtil {

    @Value("${sandbox.input-dir}")
    private String inputDir;

    public void createTestParamsFile(Map<String, Object> testParams,  UUID solutionId) throws IOException {
        File directory = new File(inputDir);
        if (!directory.exists()) {
            FileUtils.forceMkdir(directory);

        }
        String fileName = "parameters_" + solutionId.toString() + ".txt";
        String filePath = Paths.get(inputDir, fileName).toString();
        File file = new File(filePath);

        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, Object> entry : testParams.entrySet()) {
            content.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        content.append("\n");
        FileUtils.writeStringToFile(file, content.toString(), StandardCharsets.UTF_8);
    }
}
