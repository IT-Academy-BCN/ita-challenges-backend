FROM bellsoft/liberica-openjdk-alpine:21
#RUN apk add -U consul
ADD ["build/libs/itachallenge-auth.jar", "/opt/itachallenge-auth/"]

ARG GITHUB_CLIENT_ID
ARG GITHUB_CLIENT_SECRET

ENV GITHUB_CLIENT_ID=$GITHUB_CLIENT_ID
ENV GITHUB_CLIENT_SECRET=$GITHUB_CLIENT_SECRET

ENTRYPOINT ["java", "-jar", "/opt/itachallenge-auth/itachallenge-auth.jar"]
EXPOSE 7777