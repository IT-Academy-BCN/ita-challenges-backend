# 2. PROCEDIMIENTOS

## Lo primero que se debe hacer al entrar a proyecto

### 1. Añadir tu nombre y GitHub al archivo contributors.md

1. Clona el repositorio ita-challenges-backend de GitHub en tu sistema local:

         git clone https://github.com/IT-Academy-BCN/ita-challenges-backend.git
2. Cambie al directorio del repositorio clonado:

         cd ita-challenges-backend
3. Asegúrese de estar en la rama "develop". Puede verificar las ramas diponibles y su rama actual ejecutando el siguiente comando:

         git branch
4. Si no está en la rama "develop", cambie a ella ejecutando el siguiente comando:

         git checkout develop
5. Cree una nueva rama con su nombre para realizar sus cambios:

         git checkout -b nombre-de-su-rama
   Remplace "nombre-de-su-rama" con un nombre descriptivo que indique los cambios que planea realizar.


6. Abra el archivo contributors.md y agregue su nombre y su GitHub.


7. Después de hacer un git add y un git commit, realize el siguiente git push:

         git push origin nombre-de-su-rama
8. Abra el repositorio en GitHub y debería ver un mensaje que le permite crear un "pull request" desde su rama recién creada a la rama "develop". Haga clic en el enlace para crear el pull request. 


### 2. Importar data en MongoDB (Ejemplo para data del micro itachallenge-challenge)

1. Asegúrate de tener las MongoDB Tools instaladas. Si aún no las tienes, ve al apartado de “Programas necesarios” y sigue las instrucciones proporcionadas para descargarlas en tu sistema operativo.


2. Ahora que tienes las MongoDB Tools instaladas, abre la terminal de MongoDB ejecutando el siguiente comando:

         mongosh

3. Usa el siguiente comando para crear la base de datos "challenges":

         use challenges

4. Una vez dentro de la base de datos "challenges", pega el siguiente bloque de código:

         db.createUser({
         user: "admin_challenge",
         pwd: "BYBcMJEEWw5egRUo",
         roles: [
         { role: "dbOwner", db: "challenges" }
         ]
         });

5. Ejecuta el siguiente comando para ver si el usuario se ha creado correctamente:

         show users
6. Sal de la terminal de MongoDB escribiendo el siguiente comando:

         exit

7. Mueve el archivo load-data de la carpeta monogdb.init a la carpeta mongodb-test-data.
   Si estás utilizando Windows, utiliza el archivo .bat, en cambio, si estás utilizando Mac o Linux, el archivo es .sh.


8. En la terminal normal, navega a la carpeta mongodb-test-data utilizando el comando cd.


9. Para importar el documento, ejecuta el siguiente comando:

   · En Windows:

         load-data.bat

   · En Mac o Linux:

         ./load-data.sh

   Si recibes un mensaje de permiso denegado, otorga permisos de ejecución al archivo con el siguiente comando:

         chmod +x load-data.sh

10. Luego, mueve el archivo load-data.bat o load-data.sh de vuelta a su carpeta original, mongodb.init.


11. Abre MongoDB Compass, haz un "Reload Data" y ya verás la base de datos correctamente importada.


## Procedimiento diario de lunes a jueves

#### 1. Conectarse a Teams a las 9:15h
#### 2. Hacer pull de la rama "develop"
#### 3. Hacer merge de develop con la rama en la que estas trabajando
#### 4. Coffee time a las 10:15h aproximadamente

IMPORTANTE: Se espera que en proyecto estemos online con cámara encendida desde las 9:15h hasta las 13:15h, para trabajar en equipo (salvo circunstancias que lo justifiquen).


## Como trabajar con una tarjeta
### Como asignarse una tarjeta
1. En el tablero de Spring Backlog, localiza la tarjeta que deseas asignarte.
2. Haz clic en la tarjeta para abrirla y ver más detalles.
3. En la parte de la derecha, en el campo "Assigness", si aprientas "Add assigness" se abre un desplegable con todos los participantes del proyecto y ya te puedes seleccionar a ti para asignártela.
4. Una vez que te has asignado la tarjeta, tu perfil de usuario se mostrará como el responsable de la tarjeta.

### Sistema de columnas para organizar y visualizar el progreso de las tarjetas
1. Te puedes asignar cualquier targeta que esté en la columna "Todo" siempre y cuando esté libre.
2. Cuando empieces a trabajar con la targeta, la deberás mover a la columna "Doing".
3. Cuando la tarea de la targeta haya sido completada y hayas hecho la PR a la rama "develop", puedes mover la targeta a la columna "Testing" para indicar que ya está lista para ser probada.
3. En la parte de la derecha, en el campo "Assigness", si aprientas "Add assigness" se abre un desplegable con todos los participantes del proyecto y ya te puedes seleccionar a ti para asignártela.
4. Una vez que te has asignado la tarjeta, tu perfil de usuario se mostrará como el responsable de la tarjeta.

### Sistema de columnas para organizar y visualizar el progreso de las tarjetas
1. Te puedes asignar cualquier targeta que esté en la columna "Todo" siempre y cuando esté libre.
2. Cuando empieces a trabajar con la targeta, la deberás mover a la columna "Doing".
3. Cuando la tarea de la targeta haya sido completada y hayas hecho la PR a la rama "develop", puedes mover la targeta a la columna "Testing" para indicar que ya está lista para ser probada.
4. Finalmente, cuando tu PR haya sido aceptada y, por lo tanto, la tarjeta haya sido aprobada en las pruebas, esta se pasará a la columna "Done". Esto indica que la tarjeta se ha completado con éxito.

