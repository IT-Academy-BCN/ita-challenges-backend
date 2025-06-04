##!/bin/sh
##  Process to deploy manually the docker image; from root folder, execute:
##  export ENV=dev
##         REGISTRY_NAME=itacademybcn/itachallenges
##         MICROSERVICE_VERSION=x.x.x
##         GITHUB_CLIENT_ID={your_github_client_id}
##         GITHUB_CLIENT_SECRET={your_github_client_secret}
##
##       ./itachallenge-auth/build_Docker.sh
##
##  At the server, execute:
##      ./deploy_backend_dev.sh itachallenge-challenge [MICROSERVICE_VERSION]
#
## Init variables
## Init variables
#echo " ENV="${ENV}
#echo " REGISTRY_NAME="${REGISTRY_NAME}
#echo " MICROSERVICE_VERSION="${MICROSERVICE_VERSION}
#echo " GITHUB_CLIENT_ID="${GITHUB_CLIENT_ID}
#echo " GITHUB_CLIENT_SECRET="${GITHUB_CLIENT_SECRET}
#
#
#now="$(date +'%d-%m-%Y %H:%M:%S:%3N')"
#base_dir=`pwd`
#
#./gradlew :itachallenge-auth:clean && ./gradlew :itachallenge-auth:build
#
#cd itachallenge-auth
#docker build --build-arg GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID} \
#             --build-arg GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET} \
#             -t=${REGISTRY_NAME}:itachallenge-auth-${MICROSERVICE_VERSION} .
#
#
##upload image to DockerHub
#if [ ${ENV} = "dev" ] || [ ${ENV} = "pre" ];
#then
#  docker push ${REGISTRY_NAME}:itachallenge-auth-${MICROSERVICE_VERSION}
#fi


# Simulated deploy of docker image for testing the pipeline (no real push)
# Usage:
# export ENV=dev
# export REGISTRY_NAME=itacademybcn/itachallenges
# export MICROSERVICE_VERSION=2.0.1-SIMULATED
# export GITHUB_CLIENT_ID=xxxx
# export GITHUB_CLIENT_SECRET=yyyy
# ./itachallenge-auth/build_docker.sh

# Simulación de despliegue de imagen Docker para probar el pipeline (sin push real)

echo ">>> Simulating build process..."
echo " ENV=${ENV}"
echo " REGISTRY_NAME=${REGISTRY_NAME}"
MICROSERVICE_VERSION=$(grep '^version =' build.gradle | sed -E "s/version = ['\"](.+)['\"]/\\1/")

if [ -z "$MICROSERVICE_VERSION" ]; then
  echo ">>> ERROR: Could not extract MICROSERVICE_VERSION from build.gradle"
  exit 1
fi
echo " MICROSERVICE_VERSION=${MICROSERVICE_VERSION}"
echo " GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID}"
echo " GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET}"

now="$(date +'%d-%m-%Y %H:%M:%S:%3N')"
base_dir=$(pwd)

# Simulate gradle build
if [ -z "$ENV" ] || [ -z "$REGISTRY_NAME" ] || [ -z "$MICROSERVICE_VERSION" ]; then
  echo ">>> ERROR: ENV, REGISTRY_NAME, and MICROSERVICE_VERSION must be set."
  exit 1
fi

echo ">>> Running Gradle build (real)..."
./gradlew :itachallenge-auth:clean && ./gradlew :itachallenge-auth:build
if [ $? -ne 0 ]; then
  echo ">>> ERROR: Gradle build failed. Aborting simulation."
  exit 1
fi


# Simulate Docker build (no ejecución real)
echo ">>> Simulating Docker build..."
echo "cd itachallenge-auth"
echo "docker build --build-arg GITHUB_CLIENT_ID=${GITHUB_CLIENT_ID} \\"
echo "             --build-arg GITHUB_CLIENT_SECRET=${GITHUB_CLIENT_SECRET} \\"
echo "             -t=${REGISTRY_NAME}:itachallenge-auth-${MICROSERVICE_VERSION} ."
# No se ejecuta el docker build, sólo simula

# Simulate Docker push
if [ "${ENV}" = "dev" ] || [ "${ENV}" = "pre" ]; then
  echo ">>> Simulating Docker push to registry:"
  echo "    docker push ${REGISTRY_NAME}:itachallenge-auth-${MICROSERVICE_VERSION}"
  echo ">>> (Simulation only, no image actually pushed)"
else
  echo ">>> ENV is not dev or pre — skipping Docker push simulation"
fi

echo ">>> Simulation completed at ${now}"
