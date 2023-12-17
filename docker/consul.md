## <span style='color: purple;'>Desarrollo con Consul</span>

### <span style='color: green;'>Workflow de desarrollo</span>


Para trabajar en un microservicio que necesita registrarse en Consul, hay dos posibilidades:

- **Arrancar Consul en localhost con una sola instancia**, como se indica en [Arranque de Consul localhost](#arranque-de-consul-localhost), o bien...
- **Arrancar Consul utilizando contenedores Docker (cluster)**, como se indica en [Arranque de Consul Docker](#arranque-de-consul-docker)

En ambos casos, el microservicio se registra correctamente (tanto si Consul está corriendo sobre Docker como si no).
Para que el/los microservicio(s) se registre(n), en cualquiera de los casos anteriores es necesario fijar en bootstrap.yml del microservicio que se desea registrar:

````  
spring:
  cloud:
   consul:
     enabled: true
````


#### <span style='color: brown;'>Arranque de Consul localhost</span>

- Instalar previamente Consul en la máquina y establecer PATH de contexto

```
consul agent -bootstrap-expect=1 -config-file=consul/server1_standalone.json -bind 127.0.0.1 -client 127.0.0.1
```

- bootstrap-expect=1: número de nodos que se esperan en el cluster

#### <span style='color: brown;'>Arranque de Consul Docker</span>

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


<hr/>

#### <span style='color: navy;'>Utilidades Consul</span>

**NOTA:** Las bases de datos de los microservicios son privadas, por lo que no estan registran en Consul.
Para registrarlas, puede utilizarse el fichero register-[db].json, haciendo las siguientes invocaciones a API:

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





