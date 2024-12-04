# Use OpenJDK 11 as the base image
FROM openjdk:11-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the compiled CustomCompiler class and dependencies into the container
COPY CustomCompiler.class /app/
COPY CustomJavaFileManager.class /app/

# Copy the Java source file to be compiled
COPY HelloWorld.java /app/

# Command to run the custom compiler
CMD ["java", "com.itachallenge.score.compiler.CustomCompiler"]