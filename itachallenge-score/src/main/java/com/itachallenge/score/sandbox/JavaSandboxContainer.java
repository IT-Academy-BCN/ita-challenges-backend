package com.itachallenge.score.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class JavaSandboxContainer implements DockerContainerHelper {

    private static final Logger log = LoggerFactory.getLogger(JavaSandboxContainer.class);
    private static final String JAVA_SANDBOX_IMAGE = "openjdk:11-jdk-slim-sid";

    private final GenericContainer<?> javaContainer;

    public JavaSandboxContainer() {
        javaContainer = createContainer(JAVA_SANDBOX_IMAGE);
        log.info("Java Sandbox Container created");
    }

    @Override
    public GenericContainer<?> createContainer(String imageName) {
        try {
            GenericContainer<?> container = new GenericContainer<>(imageName);
            container.withFileSystemBind("/tmp/app", "/app");
            return container;
        } catch (Exception e) {
            log.error("Error creating container", e);
            return null;
        }
    }

    @Override
    public void copyFileToContainer(GenericContainer<?> container, String fileContent, String containerPath) throws IOException, InterruptedException {
        container.copyFileToContainer(Transferable.of(fileContent.getBytes()), containerPath);
        log.info("File copied to container at path: {}", containerPath);
    }

    @Override
    public void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException {
        Container.ExecResult result = container.execInContainer(command);
        log.info("stdout: {}", result.getStdout());
        log.error("stderr: {}", result.getStderr());
    }


    @Override
    public void startContainer() {
        if (!javaContainer.isRunning()) {
            javaContainer.start();
            log.info("Java Sandbox Container started");
        } else {
            log.warn("Java Sandbox Container is already running");
        }
    }

    @Override
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
                "java.io.BufferedWriter",
                "java.io.InputStream",
                "java.io.OutputStream",
                "java.io.RandomAccessFile",
                "java.io.FileOutputStream",
                "java.io.FileDescriptor"
        );
    }
}
