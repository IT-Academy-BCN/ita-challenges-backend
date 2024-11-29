package com.itachallenge.score.sandbox;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DockerOptimizedExecutorTest {

    @Autowired
    private DockerClient dockerClient;

    @Test
    public void testPullHelloWorldImageAndVerify() throws Exception {

        // Pull the image
        dockerClient.pullImageCmd("hello-world")
                .withTag("latest")
                .start()
                .awaitCompletion();

        // Verify the image was pulled
        assertDoesNotThrow(() -> {

            InspectImageResponse imageResponse = dockerClient.inspectImageCmd("hello-world:latest").exec();

            assertNotNull(imageResponse, "Image hello-world:latest should exist");

            System.out.println("Image pulled and exists: " + imageResponse.getId());

        }, "Image hello-world:latest was not pulled successfully");

    }

}
