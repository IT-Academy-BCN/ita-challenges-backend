# Use an Alpine base image
FROM alpine:latest

# Install bash and curl
RUN apk add --no-cache bash curl

# Install openjdk for testing until custom JRE is ready
RUN apk add --no-cache openjdk11

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Add the script to compile and run user code
COPY compile_and_run.sh /usr/local/bin/compile_and_run.sh
RUN chmod +x /usr/local/bin/compile_and_run.sh

# Default command to execute the script when the container starts
CMD ["compile_and_run.sh"]
