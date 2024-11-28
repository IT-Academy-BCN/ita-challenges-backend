FROM bellsoft/liberica-openjdk-alpine:21
#sólo para construcción manual
#------------------------------------------------------------------------------
#ADD ["build/libs/itachallenge-mock.jar", "/opt/itachallenge-mock/"]
#ENTRYPOINT ["java", "-jar", "/opt/itachallenge-mock/itachallenge-mock.jar"]
#------------------------------------------------------------------------------
ADD ["itachallenge-mock-1.0.5-RELEASE.jar", "/opt/itachallenge-mock/"]
ENTRYPOINT ["java", "-jar", "/opt/itachallenge-mock/itachallenge-mock-1.0.5-RELEASE.jar"]
EXPOSE 7779