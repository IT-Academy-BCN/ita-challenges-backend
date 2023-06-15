FROM bellsoft/liberica-openjdk-alpine:17.0.1
RUN apk add -U consul
ADD ["build/libs/itachallenge-challengeI-1.0.0-SNAPSHOT.jar", "/opt/itachallenge-challengeI/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-challengeI/itachallenge-challengeI-1.0.0-SNAPSHOT.jar"]
EXPOSE 7777