package com.itachallenge.score.sandbox;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.github.dockerjava.core.DockerClientBuilder.getInstance;
import static org.mockito.Mockito.mock;

class DockerOptimizedExecutorTest {

    @Test
    void testImagePulledSuccessfully() throws IOException {

        // Set up
        DockerClient dockerClient = getInstance().build();
        PullImageResultCallback mockCallback = mock(PullImageResultCallback.class);
        DockerOptimizedExecutor executor = new DockerOptimizedExecutor(mockCallback, dockerClient);

        // Execute method
        executor.execute("public class Main {}", new String[]{});

        // Test
        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter("hello-world:latest").exec();

        boolean imagePulled = images.stream().anyMatch(image ->
                image.getRepoTags() != null && java.util.Arrays.asList(image.getRepoTags()).contains("hello-world:latest"));

        assertTrue(imagePulled, "The image 'hello-world:latest' should be pulled.");

        // Cleanup
        dockerClient.removeImageCmd("hello-world:latest").withForce(true).exec();
        dockerClient.close();

    }

}
