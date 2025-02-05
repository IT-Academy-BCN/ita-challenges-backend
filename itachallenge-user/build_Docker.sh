#!/bin/sh
#  Process to deploy manually the docker image; from root folder, execute:
#       export ENV=dev
#       export REGISTRY_NAME=itacademybcn/itachallenges
#       export MICROSERVICE_VERSION=x.x.x
#       ./itachallenge-user/build_Docker.sh
#
#  At the server, execute:
#      ./deploy_backend_dev.sh itachallenge-challenge [MICROSERVICE_VERSION]

# Init variables
# Init variables
echo " ENV="${ENV}
echo " REGISTRY_NAME="${REGISTRY_NAME}
echo " MICROSERVICE_VERSION="${MICROSERVICE_VERSION}


now="$(date +'%d-%m-%Y %H:%M:%S:%3N')"
base_dir=`pwd`

./gradlew :itachallenge-user:clean && ./gradlew :itachallenge-user:build

cd itachallenge-user
docker build -t=${REGISTRY_NAME}:itachallenge-user-${MICROSERVICE_VERSION} .

#upload image to DockerHub
if [ ${ENV} = "dev" ] || [ ${ENV} = "pre" ];
then
  docker push ${REGISTRY_NAME}:itachallenge-user-${MICROSERVICE_VERSION}
fi
