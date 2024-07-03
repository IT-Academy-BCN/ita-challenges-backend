#!/bin/bash
#title  :deploy_backend_dev.sh
#In params:
#  - microservice_name
#  - microservice_upgrade_version
#description:
#bash_version: 4.2.46(1)-release or later
#===================================================================================================

microservice_name=$1;
microservice_upgrade_version=$2;

echo 'Microservice Name:'${microservice_name}
echo 'Microservice Upgrade Version:'${microservice_upgrade_version}

#Remove old version
id_container=$(docker ps -a --format '{{.ID}}\t{{.Image}}' | grep $microservice_name | cut -f1)

if docker ps --format '{{.ID}}' | grep -Eq "^${id_container}\$"; then
    echo "Container with ID ${id_container} is running. Stopping........"
    docker kill $id_container
    sleep 5
    docker rm -f $id_container
    sleep 10
else
    echo "Container with ID ${id_container} is not running"
fi

docker container prune -f
sleep 10

export MICROSERVICE_TAG=$microservice_upgrade_version
docker compose --env-file itachallenges/conf/.env.dev -f itachallenges/docker/docker-compose.dev.yml up --remove-orphans $microservice_name -d
