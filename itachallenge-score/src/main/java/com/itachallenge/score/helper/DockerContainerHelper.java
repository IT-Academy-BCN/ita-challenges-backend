package com.itachallenge.score.helper;

import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@NoArgsConstructor
public class DockerContainerHelper {

    public static GenericContainer<?> createContainer(String image) {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(image));
        container.start();
        return container;
    }

    public static void stopContainer(GenericContainer<?> container) {
        if (container != null) {
            container.stop();
        }
    }

    public static void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException {
        container.execInContainer(command);
    }

    public static void copyFileToContainer(GenericContainer<?> container, String content, String containerPath) {
        container.copyFileToContainer(Transferable.of(content.getBytes()), containerPath);
    }
}

