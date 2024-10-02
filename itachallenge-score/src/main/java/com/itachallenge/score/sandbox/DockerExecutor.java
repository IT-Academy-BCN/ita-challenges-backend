// DockerExecutor.java
package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Component
public class DockerExecutor {

    private static final String codeTemplate = "public class Main { public static void main(String[] args) { %s } }";

    public ExecutionResult executeDockerCommand(String javaCode) throws IOException, InterruptedException {
        ExecutionResult executionResult = new ExecutionResult();

        if (javaCode == null) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Java code is null");
            return executionResult;
        }

        String formattedCode = String.format(codeTemplate, javaCode);
        formattedCode = formattedCode.replace("\"", "\\\"");
        String containerName = "java-executor-container-" + UUID.randomUUID();
        String command = String.format("docker run --rm --name %s java-executor \"%s\"", containerName, formattedCode);

        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            processBuilder = new ProcessBuilder("sh", "-c", command);
        }
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        executionResult.setCompiled(exitCode == 0);
        executionResult.setExecution(exitCode == 0);
        executionResult.setMessage(output.toString().trim());

        if (exitCode != 0) {
            executionResult.setMessage("Execution failed with exit code: " + exitCode + ". Output: " + output.toString().trim());
        }

        return executionResult;
    }
}