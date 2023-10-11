## <span style='color: purple;'>Configuración del entorno Consul</span>

### <span style='color: green;'>Consul</span>

Véase [consul.md](consul.md)

### <span style='color: green;'>Apisix Gateway</span>

Inicialización está configurada con Docker. Es necesario efectuar los siguientes pasos (en este orden):

* Arranque de **apisix**
```
docker compose --env-file conf/.env.dev -f consul/docker-compose.yml up --remove-orphans apisix
```


