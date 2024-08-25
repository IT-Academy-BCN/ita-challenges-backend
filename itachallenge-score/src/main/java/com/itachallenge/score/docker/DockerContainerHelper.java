package com.itachallenge.score.docker;

import org.codehaus.janino.SimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DockerContainerHelper {

    private static final Logger log = LoggerFactory.getLogger(DockerContainerHelper.class);

    public DockerContainerHelper() {
    }

    public static GenericContainer<?> createContainer(String image) {
        log.info("Creating container with image: {}", image);
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(image));
        container.start();
        log.info("Container started with image: {}", image);
        return container;
    }

    public static void stopContainer(GenericContainer<?> container) {
        if (container != null) {
            log.info("Stopping container...");
            container.stop();
            log.info("Container stopped.");
        }
    }

    public static void executeCommand(GenericContainer<?> container, String... command) throws IOException, InterruptedException {
        log.info("Executing command in container: {}", String.join(" ", command));
        container.execInContainer(command);
    }

    public static void copyFileToContainer(GenericContainer<?> container, String content, String containerPath) {
        log.info("Copying file to container at path: {}", containerPath);
        container.copyFileToContainer(Transferable.of(content.getBytes()), containerPath);
    }

    // Specific Sandbox for Java code
    public static GenericContainer<?> createJavaSandboxContainer() {
        log.info("Creating Java sandbox container...");
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse("openjdk:11-jdk-slim"))
                .withWorkingDirectory("/home/sandbox")
                .withCommand("tail", "-f", "/dev/null");
        container.start();
        log.info("Java sandbox container started.");
        return container;
    }

    // Run Java code with a custom class loader and Janino compiler
    public static void runWithCustomClassLoader() throws Exception {
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

        // Example of using Janino to compile and execute code
        String code = "public class Test { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(customClassLoader);
        compiler.cook(code);
        Class<?> testClass = compiler.getClassLoader().loadClass("Test");
        testClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
    }
}