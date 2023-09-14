## <span style='color: purple;'>Apisix</span>

### <span style='color: green;'>Arranque de Apisix Gateway</span>

Inicialización está configurada con Docker. Es necesario efectuar los siguientes pasos (en este orden):

* Arranque de **etcd**
```
docker compose -f consul/docker-compose.yml up etcd
```

* Arranque de **apisix**
```
docker compose -f consul/docker-compose.yml up apisix
```

* Arranque de **prometheus**
```
docker compose -f consul/docker-compose.yml up prometheus
```
Prometheus está disponible en http://localhost:9090

* Arranque de **grafana**
```
docker compose -f consul/docker-compose.yml up grafana
```
Grafana está disponible en http://localhost:3000

* Arranque de **apisix-dashboard**
```
docker compose -f consul/docker-compose.yml up grafana
```
El dashboard está disponible en http://localhost:9000
