package com.itachallenge.score.docker;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DockerContainerHelper {

    private DockerContainerHelper() {
    }

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

    // Specific Sandbox for Java code
    public static GenericContainer<?> createJavaSandboxContainer() {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("sandbox-image"))
                .withWorkingDirectory("/home/sandbox")
                .withCommand("tail", "-f", "/dev/null");
        container.start();
        return container;
    }
    // Mirar si el codevalidator es necesario para no repetir la lista de librerias prohibidas
    public static void runWithCustomClassLoader() throws ClassNotFoundException {
        List<String> prohibitedClasses = Arrays.asList(
                "java.lang.System",
                "java.lang.Runtime",
                "java.io.FileInputStream",
                "java\\.io\\.PrintStream",
                "java\\.io\\.File",
                "java\\.io\\.FileReader",
                "java\\.io\\.FileWriter",
                "java\\.io\\.BufferedReader",
                "java\\.io\\.BufferedWriter"
        );

        ClassLoader parent = ClassLoader.getSystemClassLoader();
        ClassLoader customClassLoader = new CustomClassLoader(parent, prohibitedClasses);

        // Use the custom class loader
        Class<?> clazz = Class.forName("com.example.MyClass", true, customClassLoader);
    }
}