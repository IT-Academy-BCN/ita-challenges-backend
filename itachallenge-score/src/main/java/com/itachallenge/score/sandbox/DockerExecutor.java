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

    @Value("${docker.image-name}")
    private String dockerImageName;

    @Value("${docker.container-name}")
    private String containerName;

    @Value("${docker.code-template}")
    private String codeTemplate;

    @Value("${docker.timeout-seconds}")
    private long timeoutSeconds;

    @Value("${commands.windows}")
    private String windowsCommand;

    @Value("${commands.unix}")
    private String unixCommand;

    private boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    public ExecutionResult execute(String javaCode, String[] args) throws IOException, InterruptedException {
        ExecutionResult executionResult = new ExecutionResult();

        if (javaCode == null) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Java code is null");
            return executionResult;
        }

        String formattedCode;
        try {
            formattedCode = String.format(codeTemplate, javaCode);
        } catch (NullPointerException e) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Code template is null");
            return executionResult;
        }
        formattedCode = formattedCode.replace("\"", "\\\"");



        String argsBuilder = buildExecutionArguments(args);
        String command = String.format(
                "docker run --rm --name %s %s sh -c \"echo '%s' > Main.java && javac Main.java && java -Djava.security.manager -Djava.security.policy=/usr/app/restrictive.policy Main %s\"",
                containerName, dockerImageName, formattedCode, argsBuilder.toString().trim()
        );

        log.info("Executing command: {}", command);

        ProcessBuilder processBuilder = createProcessBuilder(command, isWindows ? windowsCommand : unixCommand);
        processBuilder.redirectErrorStream(true);

        try (AutoCloseableExecutor autoCloseableExecutor = new AutoCloseableExecutor(Executors.newSingleThreadExecutor())) {
            ExecutorService executor = autoCloseableExecutor.getExecutorService();
            ExecutionResult finalExecutionResult = executionResult;
            Future<ExecutionResult> future = executor.submit(() -> executeCommand(processBuilder, finalExecutionResult));

            try {
                executionResult = future.get(timeoutSeconds, TimeUnit.SECONDS);

            } catch (TimeoutException e) {
                future.cancel(true);
                handleTimeout(containerName, executionResult);
            } catch (ExecutionException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ExecutionTimedOutException("Execution was interrupted after " + timeoutSeconds);
            }
        } finally {
            cleanUpContainers(containerName);
        }
        return executionResult;
    }

    private void handleTimeout(String containerName, ExecutionResult executionResult) throws IOException, InterruptedException {
        // Intenta obtener el ID del contenedor que sigue ejecutándose
        try (AutoCloseableProcess getContainerIdProcess = new AutoCloseableProcess(
                createProcessBuilder("docker ps -q --filter name=" + containerName, isWindows ? windowsCommand : unixCommand).start());
             BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(getContainerIdProcess.getProcess().getInputStream()))) {

            String containerId = containerIdReader.readLine();

            // Si el contenedor sigue en ejecución, intenta matarlo
            if (containerId != null && !containerId.isEmpty()) {
                try (AutoCloseableProcess killProcess = new AutoCloseableProcess(
                        createProcessBuilder("docker kill " + containerId, isWindows ? windowsCommand : unixCommand).start())) {
                    killProcess.getProcess().waitFor();
                }
            }
        }

        // Actualiza el resultado con el mensaje de timeout
        String message = "Execution timed out after " + timeoutSeconds + " seconds.";
        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage(message);
    }


    private ProcessBuilder createProcessBuilder(String command, String shellCommand) {
        return new ProcessBuilder(shellCommand, isWindows ? "/c" : "-c", command);
    }

    public void cleanUpContainers(String containerName) throws IOException, InterruptedException {
        Process listContainersProcess = createProcessBuilder("docker ps -a -q --filter name=" + containerName, isWindows ? windowsCommand : unixCommand).start();
        try (BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(listContainersProcess.getInputStream()))) {
            String containerId;
            while ((containerId = containerIdReader.readLine()) != null) {
                if (!containerId.isEmpty()) {
                    createProcessBuilder("docker rm -f " + containerId, isWindows ? windowsCommand : unixCommand).start().waitFor();
                }
            }
        }
    }

    private ExecutionResult executeCommand(ProcessBuilder processBuilder, ExecutionResult executionResult) {
        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                executionResult.setCompiled(true);
                executionResult.setExecution(true);
                executionResult.setMessage(output.toString().trim());
            } else {
                executionResult.setCompiled(false);
                executionResult.setExecution(false);
                executionResult.setMessage("Execution failed with exit code " + exitCode + ". Output: " + output.toString().trim());
            }
        } catch (IOException | InterruptedException e) {
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Execution failed: TIMED OUT EXECUTION");
            Thread.currentThread().interrupt();
        }
        return executionResult;
    }
    public String buildExecutionArguments(String[] args) {
    StringBuilder argsBuilder = new StringBuilder();
    for (String arg : args) {
        argsBuilder.append(arg).append(" ");
    }
    return argsBuilder.toString().trim();
}

}