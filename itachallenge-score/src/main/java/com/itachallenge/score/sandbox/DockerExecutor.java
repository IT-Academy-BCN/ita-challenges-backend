package com.itachallenge.score.sandbox;

import com.itachallenge.score.exception.ExecutionTimedOutException;
import com.itachallenge.score.util.ExecutionResult;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

@Setter
@Getter
@NoArgsConstructor
@Component
public class DockerExecutor {

    private static final Logger log = LoggerFactory.getLogger(DockerExecutor.class);

    private static final String DOCKER_IMAGE_NAME = "openjdk:21"; //Change that image to the one you want to use
    private static final String CONTAINER = "java-executor-container";

    private static final String CODE_TEMPLATE = "public class Main { public static void main(String[] args) { %s } }"; //Can't parameterize this string.
    private static final long TIMEOUT_SECONDS = 5; //Lifetime of the container

    @Value("${commands.windows}")
    private String windowsCommand;

    @Value("${commands.unix}")
    private String unixCommand;

    private boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

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
        String command = String.format("docker run --rm --name %s %s sh -c \"echo '%s' > Main.java && javac Main.java && java Main\"", CONTAINER, DOCKER_IMAGE_NAME, formattedCode);

        log.info("Executing command: {}", command);
        cleanUpContainers(CONTAINER);

        ProcessBuilder processBuilder = createProcessBuilder(command, isWindows ? windowsCommand : unixCommand);
        processBuilder.redirectErrorStream(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutionResult finalExecutionResult = executionResult;
        Future<ExecutionResult> future = executor.submit(() -> executeCommand(processBuilder, finalExecutionResult));

        try {
            executionResult = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            Process getContainerIdProcess = createProcessBuilder("docker ps -q --filter name=" + CONTAINER, isWindows ? windowsCommand : unixCommand).start();
            BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(getContainerIdProcess.getInputStream()));
            String containerId = containerIdReader.readLine();
            if (containerId != null && !containerId.isEmpty()) {
                createProcessBuilder("docker kill " + containerId, isWindows ? windowsCommand : unixCommand).start().waitFor();
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

    private ProcessBuilder createProcessBuilder(String command, String shellCommand) {
        return new ProcessBuilder(shellCommand, isWindows ? "/c" : "-c", command);
    }

    public void cleanUpContainers(String containerName) throws IOException, InterruptedException {
        Process listContainersProcess = createProcessBuilder("docker ps -a -q --filter name=" + containerName, isWindows ? windowsCommand : unixCommand).start();
        BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(listContainersProcess.getInputStream()));
        String containerId;
        while ((containerId = containerIdReader.readLine()) != null) {
            if (!containerId.isEmpty()) {
                createProcessBuilder("docker rm -f " + containerId, isWindows ? windowsCommand : unixCommand).start().waitFor();
            }
        }
    }


    private ExecutionResult executeCommand(ProcessBuilder processBuilder, ExecutionResult executionResult) {
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                executionResult.setCompiled(true);
                executionResult.setExecution(true);
                executionResult.setMessage(output.toString().trim());
            } else {
                executionResult.setCompiled(false);
                executionResult.setExecution(false);
                executionResult.setMessage("Execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Execution failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
        return executionResult;
    }
}