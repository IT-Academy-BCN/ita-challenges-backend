FROM bellsoft/liberica-openjdk-alpine:17.0.1
RUN apk add -U consul
ADD ["build/libs/itachallenge-user-1.0.0-SNAPSHOT.jar", "/opt/itachallenge-user/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-user/itachallenge-user-1.0.0-SNAPSHOT.jar"]
EXPOSE 7777