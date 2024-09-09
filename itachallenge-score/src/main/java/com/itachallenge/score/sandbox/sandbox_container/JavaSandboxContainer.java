package com.itachallenge.score.sandbox.sandbox_container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.testcontainers.containers.GenericContainer;

import java.util.Arrays;
import java.util.List;

@Service
public class JavaSandboxContainer {

    private static final Logger log = LoggerFactory.getLogger(JavaSandboxContainer.class);

    private final GenericContainer<?> javaContainer;

    public JavaSandboxContainer() {
        this.javaContainer = new GenericContainer<>("openjdk:11-jdk-slim-sid");
        log.info("Java Sandbox Container created");
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

    public static List<String> runWithCustomClassLoader() throws ClassNotFoundException {
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

        return prohibitedClasses;
    }
}
