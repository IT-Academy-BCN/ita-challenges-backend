FROM bellsoft/liberica-openjdk-alpine:17.0.1
#RUN apk add -U consul
ADD ["build/libs/itachallenge-score.jar", "/opt/itachallenge-score/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-score/itachallenge-score.jar"]
EXPOSE 7777