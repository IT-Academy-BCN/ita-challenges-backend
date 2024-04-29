FROM bellsoft/liberica-openjdk-alpine:17.0.1
#RUN apk add -U consul
ADD ["build/libs/itachallenge-user.jar", "/opt/itachallenge-user/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-user/itachallenge-user.jar"]
EXPOSE 7777