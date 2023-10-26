FROM bellsoft/liberica-openjdk-alpine:17.0.1
ADD ["build/libs/itachallenge-mock-1.0.0-RELEASE.jar", "/opt/itachallenge-mock/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-mock/itachallenge-mock-1.0.0-RELEASE.jar"]
EXPOSE 7779