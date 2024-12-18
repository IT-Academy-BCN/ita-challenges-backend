# Use OpenJDK 21 as the base image
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled CustomCompiler class and dependencies, the custom JRE & test classes into the container
COPY custom-jre /app/custom-jre
COPY CustomCompiler.class /app/
COPY CustomJavaFileManager.class /app/
COPY OutputFileObject.class /app/
COPY HelloWorld.java /app
COPY CryptoExample.java /app