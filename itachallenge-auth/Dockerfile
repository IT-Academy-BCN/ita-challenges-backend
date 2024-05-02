FROM bellsoft/liberica-openjdk-alpine:17.0.1
#RUN apk add -U consul
ADD ["build/libs/itachallenge-auth.jar", "/opt/itachallenge-auth/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-auth/itachallenge-auth.jar"]
EXPOSE 7777