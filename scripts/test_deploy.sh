#!/bin/bash
set -e

# Ruta del archivo de configuración
ENV_FILE="conf/.env.CI.dev"

# Simulación de microservicios cambiados - normalmente se haría con git diff
# Aquí puedes cambiar la lista para probar diferentes combinaciones
CHANGED_SERVICES=("itachallenge_auth" "itachallenge_user")

echo "Simulando deploy para servicios cambiados: ${CHANGED_SERVICES[*]}"
echo ""

for SERVICE in "${CHANGED_SERVICES[@]}"; do
  echo "==== Procesando microservicio: $SERVICE ===="

  # Extraer versión del .env.CI.dev
  MICROSERVICE_VERSION=""
  while IFS='=' read -r key value; do
    if [[ "$key" == "MICROSERVICE_VERSION_$SERVICE" ]]; then
      MICROSERVICE_VERSION=$value
      break
    fi
  done < "$ENV_FILE"

  if [[ -z "$MICROSERVICE_VERSION" ]]; then
    echo "ERROR: No se encontró la versión para $SERVICE en $ENV_FILE"
    exit 1
  fi

  echo "Versión detectada para $SERVICE: $MICROSERVICE_VERSION"

  # Simular build con Gradle
  echo "Ejecutando build con Gradle para $SERVICE..."
  echo "./gradlew :$SERVICE:clean :$SERVICE:build -PMICROSERVICE_VERSION=$MICROSERVICE_VERSION"

  # Simular verificación JAR
  JAR_PATH="$SERVICE/build/libs/$SERVICE-$MICROSERVICE_VERSION.jar"
  echo "Simulando comprobación existencia JAR en: $JAR_PATH"
  # Aquí podrías incluso hacer [ -f "$JAR_PATH" ] para probar si existe (en test real)

  # Simular docker build
  echo "Simulando build de imagen Docker para $SERVICE: itacademybcn/itachallenges:${SERVICE}-${MICROSERVICE_VERSION}"

  # Simular push a Docker Hub
  echo "Simulando login en Docker Hub con usuario \$DOCKERHUB_USERNAME y token \$DOCKERHUB_TOKEN"
  echo "Simulando push de imagen itacademybcn/itachallenges:${SERVICE}-${MICROSERVICE_VERSION}"

  echo "----"
done

# Simular despliegue remoto vía SSH
echo "Simulando despliegue remoto via SSH para servicios: ${CHANGED_SERVICES[*]}"
for SERVICE in "${CHANGED_SERVICES[@]}"; do
  echo "ssh $SSH_USERNAME@$SSH_HOST_URL './deploy_backend_dev.sh $SERVICE $MICROSERVICE_VERSION'"
done

echo "Simulación completa."