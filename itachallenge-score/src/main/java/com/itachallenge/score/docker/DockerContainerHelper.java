package com.itachallenge.score.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

public class DockerContainerHelper {

    private static final Logger log = LoggerFactory.getLogger(DockerContainerHelper.class);

    private DockerContainerHelper() {
    }

    public static GenericContainer<?> createContainer(String image) {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(image));
        container.start();
        log.info("Container started with image: " + image);
        return container;
    }

    public static void stopContainer(GenericContainer<?> container) {
        if (container != null) {
            container.stop();
            log.info("Container stopped");
        }
    }

    public static void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException {
        container.execInContainer(command);
        log.info("Executed command: " + String.join(" ", command));
    }

    public static void copyFileToContainer(GenericContainer<?> container, String content, String containerPath) {
        container.copyFileToContainer(Transferable.of(content.getBytes()), containerPath);
        log.info("Copied file to container at path: " + containerPath);
    }


}