// DockerExecutor.java
package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.*;

@Getter
@NoArgsConstructor
@Component
public class DockerExecutor {

    private static final Logger log = LoggerFactory.getLogger(DockerExecutor.class);
    private static final String codeTemplate = "public class Main { public static void main(String[] args) { %s } }";
    private static final long TIMEOUT_SECONDS = 5;
    private String dockerImageName = "openjdk:21"; // Default Docker image name

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
        String command = String.format("docker run --rm --name %s %s sh -c \"echo '%s' > Main.java && javac Main.java && java Main\"", containerName, dockerImageName, formattedCode);

        log.info("Executing command: {}", command);

        // Clean up any leftover containers
        cleanUpContainers("java-executor-container-");

        ProcessBuilder processBuilder;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            processBuilder = new ProcessBuilder("sh", "-c", command);
        }
        processBuilder.redirectErrorStream(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutionResult finalExecutionResult = executionResult;
        Future<ExecutionResult> future = executor.submit(() -> {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            finalExecutionResult.setCompiled(exitCode == 0);
            finalExecutionResult.setExecution(exitCode == 0);
            finalExecutionResult.setMessage(output.toString().trim());

            if (exitCode != 0) {
                finalExecutionResult.setMessage("Execution failed with exit code: " + exitCode + ". Output: " + output.toString().trim());
            }
            return finalExecutionResult;
        });

        try {
            executionResult = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            // Get the container ID
            String osName = System.getProperty("os.name").toLowerCase();
            Process getContainerIdProcess;
            if (osName.contains("win")) {
                getContainerIdProcess = new ProcessBuilder("cmd.exe", "/c", "docker ps -q --filter name=" + containerName).start();
            } else {
                getContainerIdProcess = new ProcessBuilder("sh", "-c", "docker ps -q --filter name=" + containerName).start();
            }
            BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(getContainerIdProcess.getInputStream()));
            String containerId = containerIdReader.readLine();
            if (containerId != null && !containerId.isEmpty()) {
                // Kill the container
                if (osName.contains("win")) {
                    new ProcessBuilder("cmd.exe", "/c", "docker kill " + containerId).start().waitFor();
                } else {
                    new ProcessBuilder("sh", "-c", "docker kill " + containerId).start().waitFor();
                }
            }
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Execution timed out after " + TIMEOUT_SECONDS + " seconds");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }

        return executionResult;
    }

    private void cleanUpContainers(String namePattern) throws IOException, InterruptedException {
        String osName = System.getProperty("os.name").toLowerCase();
        Process listContainersProcess;
        if (osName.contains("win")) {
            listContainersProcess = new ProcessBuilder("cmd.exe", "/c", "docker ps -a -q --filter name=" + namePattern).start();
        } else {
            listContainersProcess = new ProcessBuilder("sh", "-c", "docker ps -a -q --filter name=" + namePattern).start();
        }
        BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(listContainersProcess.getInputStream()));
        String containerId;
        while ((containerId = containerIdReader.readLine()) != null) {
            if (!containerId.isEmpty()) {
                if (osName.contains("win")) {
                    new ProcessBuilder("cmd.exe", "/c", "docker rm -f " + containerId).start().waitFor();
                } else {
                    new ProcessBuilder("sh", "-c", "docker rm -f " + containerId).start().waitFor();
                }
            }
        }
    }
}