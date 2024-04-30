FROM bellsoft/liberica-openjdk-alpine:17.0.1
#RUN apk add -U consul
ADD ["build/libs/itachallenge-challenge.jar", "/opt/itachallenge-challenge/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-challenge/itachallenge-challenge.jar"]
EXPOSE 7777