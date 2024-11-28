package com.itachallenge.score.sandbox;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.AuthConfig;
import com.itachallenge.score.util.ExecutionResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@RequiredArgsConstructor
@Component
public class DockerOptimizedExecutor implements IDockerExecutor {

    // Constants

    private static final Logger log = LoggerFactory.getLogger(DockerOptimizedExecutor.class);

    private static final String imageName = "registry.hub.docker.com/library/hello-world:latest"; // TODO Just for testing purposes, here we will add the actual image from teh registry



    // State

    private final PullImageResultCallback pullImageResultCallback;

    private final DockerClient dockerClient;


    // Public API

    @Override
    public ExecutionResult execute(String javaCode, String[] args) {

        // The POJO to return
        ExecutionResult executionResult = new ExecutionResult();

        // javaCode cannot be null
        if (javaCode == null)
            return getJavaCodeIsNullExecutionResult(executionResult);

        // Create the credentials to authenticate in the registry
        AuthConfig authConfig = getMockAuthConfig(); // TODO Actual credentials should be instantiated, now it is irrelevant since we are testing using a public registry

        // Pull the image from the registry
        try {
            pullImageFromRegistry(authConfig);
        } catch (InterruptedException e) {
            return getIterruptedThreadExecutionResult(e, executionResult); // TODO Find a better way to handle this, talk to the architect
        }

        return getMockExecutionResult(executionResult);

    }


    // Helper methods

    private static AuthConfig getMockAuthConfig() {

        return new AuthConfig()
                .withUsername("your-username")
                .withPassword("your-password")
                .withRegistryAddress("https://your-private-registry.com");

    }

    private static @NotNull ExecutionResult getJavaCodeIsNullExecutionResult(ExecutionResult executionResult) {

        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage("Java code is null");

        return executionResult;

    }

    private static @NotNull ExecutionResult getIterruptedThreadExecutionResult(InterruptedException e, ExecutionResult executionResult) {

        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage(e.getMessage());

        return executionResult;

    }

    private static @NotNull ExecutionResult getMockExecutionResult(ExecutionResult executionResult) {

        executionResult.setCompiled(false);
        executionResult.setExecution(false);
        executionResult.setMessage("This ExecutionResult is a mock.");

        return executionResult;

    }

    private void pullImageFromRegistry(AuthConfig authConfig) throws InterruptedException {

        dockerClient.pullImageCmd(imageName)
                .withAuthConfig(authConfig)
                .exec(pullImageResultCallback) // TODO This bean is a mock
                .awaitCompletion(); // TODO This should be consented with the architect

    }

}
