#!/bin/bash
set -e

ENV_FILE="conf/.env.CI.dev"

# Detectar commits de comparación
BASE_COMMIT=${1:-HEAD~1}
TARGET_COMMIT=${2:-HEAD}

echo "Detectando cambios entre commits: $BASE_COMMIT...$TARGET_COMMIT"
CHANGED_FILES=$(git diff --name-only "$BASE_COMMIT" "$TARGET_COMMIT")

echo "Archivos modificados:"
echo "$CHANGED_FILES"
echo ""

# Extraer microservicios modificados
CHANGED_SERVICES=$(echo "$CHANGED_FILES" | grep -E '^(itachallenge-auth|itachallenge-user|itachallenge-challenge|itachallenge-document|itachallenge-mock)/' | cut -d/ -f1 | sort -u)

if [[ -z "$CHANGED_SERVICES" ]]; then
  echo "⚠️  No se detectaron microservicios cambiados. Fin del script."
  exit 0
fi

echo "Microservicios detectados como cambiados:"
echo "$CHANGED_SERVICES"
echo ""

for SERVICE in $CHANGED_SERVICES; do
  echo "==== Procesando microservicio: $SERVICE ===="

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
  echo "Ejecutando build con Gradle para $SERVICE..."
  echo "./gradlew :$SERVICE:clean :$SERVICE:build -PMICROSERVICE_VERSION=$MICROSERVICE_VERSION"

  JAR_PATH="$SERVICE/build/libs/$SERVICE-$MICROSERVICE_VERSION.jar"
  echo "Simulando comprobación existencia JAR en: $JAR_PATH"

  echo "Simulando build de imagen Docker: itacademybcn/itachallenges:${SERVICE}-${MICROSERVICE_VERSION}"
  echo "Simulando login y push a Docker Hub"
  echo "docker push itacademybcn/itachallenges:${SERVICE}-${MICROSERVICE_VERSION}"

  echo "----"
done

# Simular despliegue remoto
echo "Simulando despliegue remoto via SSH:"
for SERVICE in $CHANGED_SERVICES; do
  echo "ssh \$SSH_USERNAME@\$SSH_HOST_URL './deploy_backend_dev.sh $SERVICE $MICROSERVICE_VERSION'"
done

echo "✅ Simulación completa."