package com.itachallenge.score.sandbox;

import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public interface DockerContainerHelper {

    GenericContainer<?> createAndRunContainer(String imageName);

    void copyFileToContainer(GenericContainer<?> container, String fileContent, String containerPath) throws IOException, InterruptedException;

    void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException;

    void startContainer();

    void stopContainer();
}