##!/bin/bash
##title  :deploy_backend_dev.sh
##In params:
##  - microservice_name
##  - microservice_upgrade_version
##description:
##bash_version: 4.2.46(1)-release or later
##===================================================================================================
#
#microservice_name=$1;
#microservice_upgrade_version=$2;
#
#echo 'Microservice Name:'${microservice_name}
#echo 'Microservice Upgrade Version:'${microservice_upgrade_version}
#
##Remove old version
#id_container=$(docker ps -a --format '{{.ID}}\t{{.Image}}' | grep $microservice_name | cut -f1)
#
#if docker ps --format '{{.ID}}' | grep -Eq "^${id_container}\$"; then
#    echo "Container with ID ${id_container} is running. Stopping........"
#    docker kill $id_container
#    sleep 5
#    docker rm -f $id_container
#    sleep 10
#else
#    echo "Container with ID ${id_container} is not running"
#fi
#
#docker container prune -f
#sleep 10
#
#export MICROSERVICE_TAG=$microservice_upgrade_version
#docker compose --env-file itachallenges/conf/.env.dev -f itachallenges/docker/docker-compose.dev.yml up --remove-orphans $microservice_name -d


# Simulated deploy of backend microservice (no actual container operations)

microservice_name=$1
microservice_upgrade_version=$2

echo ">>> [SIMULATION] Microservice Name: ${microservice_name}"
echo ">>> [SIMULATION] Microservice Upgrade Version: ${microservice_upgrade_version}"

# Simulate stopping and removing old container
echo ">>> [SIMULATION] Searching for running container with name ${microservice_name}..."
id_container=$(docker ps -a --format '{{.ID}}\t{{.Image}}' | grep $microservice_name | cut -f1)

if [ -n "$id_container" ]; then
    echo ">>> [SIMULATION] Container with ID ${id_container} is assumed to be running."
    echo ">>> [SIMULATION] Simulating: docker kill $id_container"
    echo ">>> [SIMULATION] Simulating: docker rm -f $id_container"
    echo ">>> [SIMULATION] Sleeping 10 seconds to simulate removal delay..."
    sleep 1
else
    echo ">>> [SIMULATION] No running container found for ${microservice_name}"
fi

# Simulate container cleanup
echo ">>> [SIMULATION] Simulating: docker container prune -f"
sleep 1

# Simulate docker-compose up
export MICROSERVICE_TAG=$microservice_upgrade_version
echo ">>> [SIMULATION] Would execute:"
echo "    docker compose --env-file itachallenges/conf/.env.dev -f itachallenges/docker/docker-compose.dev.yml up --remove-orphans $microservice_name -d"

echo ">>> [SIMULATION] Deploy process simulated successfully"
