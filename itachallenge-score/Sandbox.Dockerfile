# Use an Alpine base image
FROM alpine:3.20.3

# Install bash and curl - it will be JDK/JRE from Michel & Joan & jq
RUN apk add --no-cache bash curl jq openjdk11

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Create the /data directory for shared user code file
RUN mkdir -p /data

# Declare the volume for shared data
VOLUME /data

# Add the script to compile and run user code it will be shell script from David
COPY sandboxRunner.sh /usr/local/bin/sandboxRunner.sh
RUN chmod +x /usr/local/bin/sandboxRunner.sh


# Default command to execute the script and keep the container running
CMD ["/bin/bash", "-c", "/usr/local/bin/sandboxRunner.sh && tail -f /dev/null"]