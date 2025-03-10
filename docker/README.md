## <span style='color: purple;'>Configuración del entorno Consul</span>

### <span style='color: green;'>Consul</span>

Véase [consul.md](consul.md)

### <span style='color: green;'>Apisix Gateway</span>

La inicialización está configurada con Docker. Para arrancar la aplicación con Docker en local, es necesario efectuar 
los siguientes pasos (en este orden, ejemplo con Microservicio Mock):

* Creación de la imagen del microservicio (si no existe):
```
./itachallenge-mock/build_Docker.sh conf/.env.local
```

* Arranque de **apisix** y otros containers
```
docker compose --env-file conf/.env.local -f consul/docker-compose.yml up --remove-orphans itachallenge-mock
docker compose --env-file conf/.env.local -f consul/docker-compose.yml up --remove-orphans apisix-gateway
```


