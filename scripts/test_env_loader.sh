#!/bin/bash

# Simular que se detectó un microservicio cambiado
changed_services="auth"

# Ruta a tu archivo .env.CI.dev
ENV_FILE="conf/.env.CI.dev"

echo "Cargando variables para microservicio: $changed_services"
for SERVICE in $changed_services; do
  while IFS='=' read -r key value; do
    if [[ "$key" == *"_$SERVICE" ]]; then
      new_key=$(echo "$key" | sed "s/_$SERVICE//")
      export "$new_key=$value"
      echo "$new_key=$value"
    fi
  done < "$ENV_FILE"
done

# Mostrar las variables cargadas
echo ""
echo "Variables cargadas:"
echo "MICROSERVICE_DEPLOY=$MICROSERVICE_DEPLOY"
echo "MICROSERVICE_VERSION=$MICROSERVICE_VERSION"

# Simular uso de estas variables
echo ""
echo "Simulando build para $MICROSERVICE_DEPLOY con versión $MICROSERVICE_VERSION"
