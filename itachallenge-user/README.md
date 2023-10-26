
## ITA User

* Para habilitar / deshabilitar el registro Consul, modificar el valor de la propiedad `spring.cloud.consul.enabled` en el fichero `bootstrap.yml` (true/false)

#### Inicialización de MongoDB

Es necesario realizar los siguientes pasos para inicializar la base de datos MongoDB local:
[^1] Crear un usuario administrador Mongo (véase https://www.mongodb.com/docs/manual/tutorial/configure-scram-client-authentication/), que deberá autenticarse sobre bd admin (por defecto usa test)
- Securizar la db (por defecto no trae usuario ni password). Editar mongod.conf y añadir:
```
security:
    authorization: enabled
```
- Conectar con usuario root Mongo con authenticationDatabase admin
```
mongosh --port 27017 --authenticationDatabase "admin" -u "rootMongoDb" -p
```
- Cambiar a db admin
```
use admin
```
- Crear un usuario administrador con permisos sobre la base de datos `users`, con el comando existente en el file mongo-init.js
```
db.createUser({
    user: "admin_user",
    pwd: "mypassword",
    roles: [
      { role: "dbOwner", db: "users" }
    ]
  });
```
- Conectar con el nuevo usuario creado
```
mongosh --port 27017 -u admin_user --authenticationDatabase "admin" -p
```
- Crear la nueva collection
```
db.createCollection("solutions");
```
- Desde fuera de db, importar el/los files de test en la base de datos `users`
```
mongoimport --db=users --username admin_user --authenticationDatabase admin --password mypassword --collection=solutions --jsonArray --file=user_score.json
```

### Configuración local de Redis 

##### Windows

- Instalar BBDD redis en la maquina y establecer PATH de contexto
- Crear redis-ITA.conf con las siguientes lineas de seguridad:
  - Para establecer una autenticación obligatoria para acceder a la bbdd  

```
  requirepass << password >>
```    

  - Establecer una seguridad adicional al renombrar algunos comandos considerados peligrosos (si se quiere usar el comando config se tiene que escribir ita_config)

```
  rename-command CONFIG ITA_CONFIG
```   

- Levantar el servidor de redis con el fichero de configuración creado indicando donde se encuentra

```
redis-server c:/.../redis-ITA.conf
```

##### UNIX based (Mac / Linux)

- Modificar redis.conf en directorio de instalación
- Añadir autenticación obligatoria
```
  requirepass << password >>
``` 
- Reinicio del servicio
```
sudo systemctl restart redis
```

#### Import data to Redis

### Use redis-cli to Execute the Script:
* redis-cli -h localhost -p 6379 < usersRedis.txt

### **Important**
When you execute the redis-cli command to import the data, make sure you are in the same directory as the ### 'usersRedis' file or provide the full path to the file in the command:
* cd /path/to/my_project/
* redis-cli -h localhost -p 6379 < users

Ensure that your Redis server is running and accessible on the specified host and port

For more info check [Import Data into Redis](https://developer.redis.com/guides/import/)

##### Spring Boot Actuator

- http://localhost:8762/actuator/health (debe responder {"status":"UP"})
- http://localhost:8762/actuator/auditevents
- http://localhost:8762/actuator/beans
- http://localhost:8762/actuator/conditions
- http://localhost:8762/actuator/configprops
- http://localhost:8762/actuator/env
- http://localhost:8762/actuator/heapdump (genera volcado de heap para descarga)
- http://localhost:8762/actuator/httptrace
- http://localhost:8762/actuator/info
- http://localhost:8762/actuator/loggers
- http://localhost:8762/actuator/metrics
- http://localhost:8762/actuator/mappings
- http://localhost:8762/actuator/scheduledtasks
- http://localhost:8762/actuator/threaddump
