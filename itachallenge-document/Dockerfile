FROM bellsoft/liberica-openjdk-alpine:17.0.1
#RUN apk add -U consul
ADD ["build/libs/itachallenge-document.jar", "/opt/itachallenge-document/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-document/itachallenge-document.jar"]
EXPOSE 7777