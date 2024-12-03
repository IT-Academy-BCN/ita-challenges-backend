# Use Alpine as the base image
FROM alpine:latest

# Copy the custom JRE to the container
COPY custom-jre /custom-jre

# Set the JAVA_HOME environment variable to the custom JRE
ENV JAVA_HOME=/custom-jre
ENV PATH=$JAVA_HOME/bin:$PATH

# Set the working directory
WORKDIR /app

# Copy the compiled CustomCompiler class and dependencies into the container
COPY build/classes/java/main/com/itachallenge/score/compiler/CustomCompiler.class /app/
COPY build/classes/java/main/com/itachallenge/score/compiler/CustomJavaFileManager.class /app/

# Copy the Java source file to be compiled
COPY custom-jre/test/HelloWorld.java /app/

# Command to run the custom compiler
CMD ["java", "com.itachallenge.score.compiler.CustomCompiler"]