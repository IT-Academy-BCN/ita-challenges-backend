FROM bellsoft/liberica-openjdk-alpine:21
#RUN apk add -U consul
ADD ["build/libs/itachallenge-user.jar", "/opt/itachallenge-user/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-user/itachallenge-user.jar"]
EXPOSE 7777