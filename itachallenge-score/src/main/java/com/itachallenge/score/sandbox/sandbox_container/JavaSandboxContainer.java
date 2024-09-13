package com.itachallenge.score.sandbox.sandbox_container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class JavaSandboxContainer implements DockerContainerHelper {

    private static final Logger log = LoggerFactory.getLogger(JavaSandboxContainer.class);

    private final GenericContainer<?> javaContainer;

    public JavaSandboxContainer() {
        this.javaContainer = new GenericContainer<>("openjdk:11-jdk-slim-sid").withFileSystemBind("/tmp/app", "/app");
        log.info("Java Sandbox Container created");
    }

    @Override
    public GenericContainer<?> createContainer(String imageName) {
        return new GenericContainer<>(imageName).withFileSystemBind("/tmp/app", "/app");
    }

    @Override
    public void copyFileToContainer(GenericContainer<?> container, String fileContent, String containerPath) throws IOException, InterruptedException {
        container.execInContainer("sh", "-c", "echo '" + fileContent + "' > " + containerPath);
    }

    @Override
    public void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException {
        container.execInContainer(command);
    }

    @Override
    public void stopContainer(GenericContainer<?> container) {
        if (container.isRunning()) {
            container.stop();
        }
    }

    public void startContainer() {
        if (!javaContainer.isRunning()) {
            javaContainer.start();
            log.info("Java Sandbox Container started");
        } else {
            log.warn("Java Sandbox Container is already running");
        }
    }

    public void stopContainer() {
        if (javaContainer.isRunning()) {
            javaContainer.stop();
            log.info("Java Sandbox Container stopped");
        } else {
            log.warn("Java Sandbox Container is not running");
        }
    }

    public GenericContainer<?> getContainer() {
        return javaContainer;
    }

    public static List<String> getProhibitedClasses() {
        return Arrays.asList(
                "java.lang.System",
                "java.lang.Runtime",
                "java.io.FileInputStream",
                "java.io.PrintStream",
                "java.io.File",
                "java.io.FileReader",
                "java.io.FileWriter",
                "java.io.BufferedReader",
                "java.io.BufferedWriter"
        );
    }
}