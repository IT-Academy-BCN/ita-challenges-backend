## Desarrollo con Consul

#### Arranque de Consul localhost (desarrollo)

- Instalar previamente Consul en la máquina y establecer PATH de contexto

```
consul agent -bootstrap-expect=1 -config-file=consul/server1_standalone.json -bind 127.0.0.1 -client 127.0.0.1
```

- bootstrap-expect=1: número de nodos que se esperan en el cluster

- Verificar que todos los microservicios que se desean registrar tienen configuración en bootstrap.yml:

````  
spring:
  cloud:
   consul:
     enabled: true
````

#### Arranque de Consul Docker

- Arrancar Docker en la máquina (UNIX based)
```
sudo systemctl start docker 
```
- Descargar imagen Docker. Véase [documentación oficial](https://hub.docker.com/_/consul).
```
docker pull consul
```

- Arrancar cluster consul (desde directorio raíz)
```
docker compose -f consul/docker-compose.yml up -d --remove-orphans consul-server1 consul-server2 consul-server3
```

- http://localhost:8500 debe mostrar consola de Administración Consul 


#### Utilidades Consul
 
- **Registrar servicios externos**. Para registrar las DB, pueden utilizarse los files register-[db].json y deregister-[db].json
```
curl --request PUT --data @consul/register-mysql.json localhost:8500/v1/catalog/register
```
- **Desregistrar servicios externos**. Para registrar las DB, pueden utilizarse los files register-[db].json y deregister-[db].json
```
curl --request PUT --data @consul/deregister-mysql.json localhost:8500/v1/catalog/deregister
//otra posibilidad
curl --request PUT localhost:8500/v1/agent/service/deregister/{service_Id}
```

![Administracion Consul](../img/assets.jpg)


## Trabajo con Consul & Docker

### Inicialización de contenedores

#### Creación de imágenes de cada microservicio
```
./build_Docker.sh
```
#### MongoDB

La configuración para la inicialización de la base de datos Mongo está incluida en docker-compose.yml (sobreescribe application.yml)

##### Utilidades Docker

- Arranque instancia MongoDB (desde directorio raíz, versión en docker-compose.yml):
```
docker compose up -d itachallenge-mongodb
```

- Entrar en contenedor accediendo a consola bash (para conectar por consola Mongo shell, p.ej.):
```
docker exec -it [containerID] bash
```

- Verificación de inicialización Mongo (desde dentro del contenedor): ejecutar desde cmd
```
mongosh --username [user] --password [pwd]  --authenticationDatabase [dbUsers] [dbUsers] --eval "db.adminCommand({ listDatabases: 1 })"
```

- Verificación de inicialización Mongo (desde fuera del contenedor):

```
docker exec -it [containerID] mongosh --username admin_itachallenge --password [password]  --authenticationDatabase [dbUsers] [dbUsers] --eval "db.adminCommand({ listDatabases: 1 })"
```





