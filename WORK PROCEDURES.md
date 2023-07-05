# 2. PROCEDIMIENTOS

## Lo primero que se debe hacer al entrar a proyecto

### 1. Hacer un pull de la rama "develop" del proyecto
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

### 1. Conectarse a Teams a las 9:15h
### 2. Hacer pull de la rama "develop"
### 3. Hacer merge de develop con la rama en la que estas trabajando
### 4. Coffee time a las 10:15h aproximadamente

IMPORTANTE: Se espera que en proyecto estemos online con cámara encendida desde las 9:15h hasta las 13:15h, para trabajar en equipo (salvo circunstancias que lo justifiquen).


