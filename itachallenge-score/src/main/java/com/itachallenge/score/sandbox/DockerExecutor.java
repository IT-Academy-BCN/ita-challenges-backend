package com.itachallenge.score.sandbox;

import com.itachallenge.score.sandbox.exception.ExecutionTimedOutException;
import com.itachallenge.score.util.ExecutionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

@Getter
@NoArgsConstructor
@Component
public class DockerExecutor {

    private static final Logger log = LoggerFactory.getLogger(DockerExecutor.class);
    private static final String CODE_TEMPLATE = "public class Main { public static void main(String[] args) { %s } }";
    private static final long TIMEOUT_SECONDS = 5;
    private String dockerImageName = "openjdk:21";
    private String containerName = "java-executor-container";

    public ExecutionResult execute(String javaCode) throws IOException, InterruptedException {
        ExecutionResult executionResult = new ExecutionResult();

        if (javaCode == null) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Java code is null");
            return executionResult;
        }
        String formattedCode = String.format(CODE_TEMPLATE, javaCode);
        formattedCode = formattedCode.replace("\"", "\\\"");
        String command = String.format("docker run --rm --name %s %s sh -c \"echo '%s' > Main.java && javac Main.java && java Main\"", containerName, dockerImageName, formattedCode);

        log.info("Executing command: {}", command);

        // Clean up any leftover containers
        cleanUpContainers(containerName);

        ProcessBuilder processBuilder = createProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutionResult finalExecutionResult = executionResult;
        Future<ExecutionResult> future = executor.submit(() -> executeCommand(processBuilder, finalExecutionResult));

        try {
            executionResult = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            // Get the container ID
            Process getContainerIdProcess = createProcessBuilder("docker ps -q --filter name=" + containerName).start();
            BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(getContainerIdProcess.getInputStream()));
            String containerId = containerIdReader.readLine();
            if (containerId != null && !containerId.isEmpty()) {
                // Kill the container
                createProcessBuilder("docker kill " + containerId).start().waitFor();
            }
            String message = "Execution timed out after " + TIMEOUT_SECONDS + " seconds";
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage(message);
        } catch (ExecutionException e) {
            throw new ExecutionTimedOutException(executionResult.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutionTimedOutException("Execution was interrupted after " + TIMEOUT_SECONDS + " seconds");
        } finally {
            executor.shutdown();
        }
        return executionResult;
    }

    private ProcessBuilder createProcessBuilder(String command) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            return new ProcessBuilder("sh", "-c", command);
        }
    }

    private ExecutionResult executeCommand(ProcessBuilder processBuilder, ExecutionResult executionResult) throws IOException, InterruptedException {
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

    private void cleanUpContainers(String namePattern) throws IOException, InterruptedException {
        Process listContainersProcess = createProcessBuilder("docker ps -a -q --filter name=" + namePattern).start();
        BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(listContainersProcess.getInputStream()));
        String containerId;
        while ((containerId = containerIdReader.readLine()) != null) {
            if (!containerId.isEmpty()) {
                createProcessBuilder("docker rm -f " + containerId).start().waitFor();
            }
        }
    }
}