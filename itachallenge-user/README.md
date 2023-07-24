
## ITA Score

* Para habilitar / deshabilitar el registro Consul, modificar el valor de la propiedad `spring.cloud.consul.enabled` en el fichero `bootstrap.yml` (true/false)

#### Inicialización de MongoDB

Es necesario realizar los siguientes pasos para inicializar la base de datos MongoDB local:
- Crear un usuario administrador Mongo (véase https://www.mongodb.com/docs/manual/tutorial/configure-scram-client-authentication/), que deberá autenticarse sobre bd admin (por defecto usa test)
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
- Crear un usuario administrador con permisos sobre la base de datos `challenges-users`, con el comando existente en el file mongo-init.js
```
db.createUser({
    user: "admin_challenges_user",
    pwd: "mypassword",
    roles: [
      { role: "dbOwner", db: "challenges-users" }
    ]
  });
```
- Conectar con el nuevo usuario creado
```
mongosh --port 27017 -u admin_challenges_user --authenticationDatabase "admin" -p
```
- Crear la nueva collection
```
db.createCollection("users");
```
- Desde fuera de db, importar el/los files de test en la base de datos `challenges-users`
```
mongoimport --db=challenges-users --username admin_challenges_user --authenticationDatabase admin --password mypassword --collection=users --jsonArray --file=user_score.json
```

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