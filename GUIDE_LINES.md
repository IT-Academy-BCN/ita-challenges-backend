# IT-Challenge Style Guide

## Table of Contents

1. [**PROJECT'S LINKS**](#1-project's-links)

   1.1 [Temp](#11-temp)  

2. [**WORK PROCEDURES**](#2-work-procedures)

   2.1 [Welcome Procedure](#21-welcome-procedure)\
   2.2 [Daily Procedure](#22-daily-procedure)\
   2.3 [Work with cards Procedure](#23-work-with-cards-procedure)\
   2.4 [Work with cards Procedure](#24-make-a-pr)

3. [**URL NORMALIZATION**](#3-url-normalization)

   3.1 [Convencions](#31-convencions)

4. [**DEFINITIONS OF METHOD, CLASS, ETC...**](#4-definitions-of-method,-class,-etc...)\
   4.1 [Package names](#41-package-names)\
   4.2 [Class names](#42-class-names)\
   4.2.1 [Test Class names](#421-test-class-names)\
   4.2.2 [Interface Class names](#422-interface-class-names)\
   4.3 [Method names](#43-method-names)\
   4.3.1 [Test Method names](#431-test-method-names)\
   4.4 [Constant names](#44-constant-names)\
   4.5 [Local variable names](#45-local-variable-names)\
   4.5.1 [Temporary "throwaway" variables](#451-temporary-"throwaway"-variables)\
   4.6 [Type variable names](#46-type-variable-names)\
   4.7 [Camel case: defined](#47-camel-case:-defined)  
   
5. [**LIBRARIES USED**](#5-libraries-used)

   5.1 [Temp](#51-temp)

6. [**REQUIRED PROGRAMS**](#6-required-programs)

   6.1 [Requierd programs](#61-required-programs)

7. [**RECOMMENDED PROGRAMS**](#7-recommended-programs)

   7.1 [Temp](#71-temp)

----------------------------------------------------------------

# 1. PROJECT'S LINKS

## 1.1 Temp

----------------------------------------------------------------

# 2. WORK PROCEDURES

## 2.1 Welcome Procedure
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


## 2.2 Daily Procedure
1. Conectarse a Teams a las 9:15h
2. Hacer pull de la rama "develop"
3. Hacer merge de develop con la rama en la que estás trabajando
4. Coffee time a las 10:15h aproximadamente

IMPORTANTE: Se espera que en proyecto estemos online con cámara encendida desde las 9:15h hasta las 13:15h, para trabajar en equipo (salvo circunstancias que lo justifiquen).

## 2.3 Work with cards Procedure
## Como trabajar con una tarjeta
### Como asignarse una tarjeta
1. En el tablero de Spring Backlog, localiza la tarjeta que deseas asignarte.
2. Haz clic en la tarjeta para abrirla y ver más detalles.
3. En la parte de la derecha, en el campo "Assigness", si aprietas "Add assigness" se abre un desplegable con todos los participantes del proyecto y ya te puedes seleccionar a ti para asignártela.
4. Una vez que te has asignado la tarjeta, tu perfil de usuario se mostrará como el responsable de la tarjeta.

### Sistema de columnas para organizar y visualizar el progreso de las tarjetas
1. Te puedes asignar cualquier tarjeta que esté en la columna "Todo" siempre y cuando esté libre.
2. Cuando empieces a trabajar con la tarjeta, la deberás mover a la columna "Doing".
3. Cuando la tarea de la tarjeta haya sido completada y hayas hecho la PR a la rama "develop", puedes mover la tarjeta a la columna "Testing" para indicar que ya está lista para ser probada.
4. Finalmente, cuando tu PR haya sido aceptada y, por lo tanto, la tarjeta haya sido aprobada en las pruebas, esta se pasará a la columna "Done". Esto indica que la tarjeta se ha completado con éxito.

### Ya tengo una targeta asignada. ¿Ahora que hago?
1. Abre tu terminal o línea de comandos y navega hasta el directorio de tu proyecto. Asegúrate de estar en la rama "develop".


2. Crea una nueva rama utilizando el formato "feature#numeroDeLaTarjeta". Se refiere al número de la tarjeta del Spring Backlog. Por ejemplo:

         git checkout -b feature#123
3. Ahora puedes comenzar a realizar los cambios en tu rama.


4. Una vez hayas realizado las modificaciones necesarias haz un push de tu rama al repositorio remoto. Si estuviésemos en la feature#123 deberíamos hacer:

         git push origin feature#123

5. Finalmente, ve a GitHub donde se encuentra tu repositorio y crea un "pull request" desde tu rama "feature#123" hacia la rama "develop".

## 2.4 Make a PR
1. En la página de creación de a pull request, selecciona la rama base y la rama comparada:
   - La rama base es la rama a la que deseas fusionar tus cambios. En este caso es la rama "develop".
   - La rama comparada es la rama que contiene tus cambios.
2. Proporciona una descripción de los cambios que has realizado. Sé claro y conciso.
3. Cuando hayas terminado de completar la información de la pull request, haz clic en el botón verde "Create pull request" para crearla.
4. A continuación verás como se hace un análisis del build. Si el análisis pasa con éxito, se mostrará un tick verde en el resultado. Por el contrario, si detecta problemas en el código, se mostrará una aspa roja. En este caso, aprieta en "Details" y observa donde se encuentra el problema.
5. SonarCloud también va a analizar tu código. Te proporcionará información sobre los bugs, vulnerabilities, security hotspots y code smell. El converage debe ser igual o superior al 80.0%.
6. En caso de que debas revisar y corregir algunos problemas, actualiza tu rama local con los cambios y haz push nuevamente a la rama remota correspondiente.
7. La pull request se actualizará automáticamente con los nuevos cambios realizados en tu rama.

## 2.5 Scrum metodology
https://scrumguides.org/

![plot](./img/VER5-scrum-framework_2020.jpg)

----------------------------------------------------------------

# 3. URL NORMALIZATION
En este proyecto, es importante seguir ciertas convenciones al establecer la estructura y nomenclatura de las URL's utilizadas en el backend.

## 3.1 Convencions

A continuación, se presentan las convenciones que deben seguirse al normalizar las URL's:

- **Pluralización**: Se recomienda utilizar nombres plurales para los recursos en las URL's. Por ejemplo, en lugar de "/challenge", se utilizaría "/challenges".

- **Uso de sustantivos**: Se deben utilizar sustantivos en lugar de verbos en los nombres de los recursos para mantener una estructura coherente. Por ejemplo, "/challenges" en lugar de "/obtener-challenges".

- **Separadores**: Se ha establecido el uso de guiones ("-") como separador para las palabras en las URL's. Por ejemplo, "/bcn-zones"

- **Consistencia en minúsculas**: Las URL's deben seguir una convención de minúsculas. Por ejemplo, "/challenges" en minúsculas en lugar de "/Challenges" o "/CHALLENGES".

- **Evitar caracteres especiales**: Se deben evitar los caracteres especiales en las URL's y, en su lugar, utilizar caracteres alfanuméricos.

- **Orden jerárquico**: Si hay una jerarquía en los recursos, se debe reflejar en la estructura de las URL's. Por ejemplo, "/challenges/{challengeId}/update" para obtener los desafios del usuario.

- **Versionado**: Si se requiere versionar la API, se debe considerar incluir la versión en la URL. Por ejemplo, "/itachallenge/api/v1/challenge" para la versión 1 de la API de challenges.

- **Evitar verbos en URL's**: En general, se recomienda evitar incluir verbos en las URL's y utilizar los métodos HTTP adecuados para realizar acciones en los recursos.

- **Consistencia con nombres de atributos**: Los nombres de atributos utilizados en las URL's deben ser coherentes con los nombres utilizados en el modelo de datos.

- **Evitar URL's demasiado largas**: Se debe evitar el uso de URL's excesivamente largas y buscar mantenerlas concisas y significativas.


## Ejemplos

Aquí se presentan algunos ejemplos de cómo deben estructurarse las URL's según las convenciones establecidas:

- `/challenges` - Obtener una lista de todos los challenges.
- `/challenges/{challengeId}` - Obtener los detalles del challenge con ID 550e8400-e29b-41d4-a716-446655440000.
- `/challenges/{challengeId}/update` - Editar los detalles del challenge con ID 550e8400-e29b-41d4-a716-446655440000.

----------------------------------------------------------------

# 4. DEFINITIONS OF METHOD, CLASS, ETC...

## 4.1 Package names
#### RULES
    - All in Lowercase
    - Only letters & digits

#### EXEMPLE
    - com.itachallenge.user
    - exception
    - helper

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.1 package names](https://google.github.io/styleguide/javaguide.html#s5.2.1-package-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.2 Class names
#### RULES
    - UpperCamelCase
    - Only letters & digits

#### EXEMPLE
    - UserController
    - PropertiesConfig

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.2 class names](https://google.github.io/styleguide/javaguide.html#s5.2.2-class-names)

### 4.2.1 Test Class names
#### RULES
    - UpperCamelCase
    - End with 'Test'
    - Only letters & digits

#### EXEMPLE
    - ChallengeControllerTest
    - ResourceHelperTest

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.2 class names](https://google.github.io/styleguide/javaguide.html#s5.2.2-class-names)

### 4.2.2 Interface Class names
#### RULES
    - UpperCamelCase
    - Only letters & digits

----------------------------------------------------------------
----------------------------------------------------------------
## 4.3 Method names
#### RULES
    - lowerCamelCase 
    - Only letters & digits

#### EXEMPLE
    - isValidUUID
    - initReactorHttpClient

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.3 method names](https://google.github.io/styleguide/javaguide.html#s5.2.3-method-names)

## 4.3.1 Test Method names
#### RULES
    - lowerCamelCase
    - Only letters & digits
    - Ended with '_test'

#### EXEMPLE
    - getChallengeId_test
    - findAll_test

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.3 method names](https://google.github.io/styleguide/javaguide.html#s5.2.3-method-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.4 Constant names
#### RULES
    - Uppercase letters
    - Only letters & digits
    - Word separete with a single underscore '_'

#### EXEMPLE
    - static final int BEST_YEAR = 1977;
    - static final String BEST_MONTH = "February";

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.4 constant names](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.5 Local variable names
#### RULES
    - lowerCamelCase
    - Start with letter
    - Variable names should be short yet meaningful  

#### EXEMPLE
    - int  = 1977;
    - static final String BEST_MONTH = "February";

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.4 constant names](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names)

### 4.5.1 Temporary "throwaway" variables
#### RULES
    - Only use for temporary "throwaway" variables
    - lower case

#### EXEMPLE
    - int   => i, j, k, m and n
    - char  => c, d and e

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.6 Type variable names
#### RULES
    - A single capital letter, optionally followed by a single numeral
    - A name in the form used for classes

#### EXEMPLE
    - E, T, X, T2
    - RequestT, ChallengeT

#### LINKS
[Google Java Style Guide: 5.2.8 Type variable names](https://google.github.io/styleguide/javaguide.html#s5.2.8-type-variable-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.7 Camel case: defined
#### RULES
    - only letters & digits
    - UpperCamelCase
        - Every first letter of every word are Upper letter
    - lowerCamelCase
        - The first letter of each word is in lowercase, except for the first word which starts 
          with a lowercase letter 

#### EXEMPLE
    - UpperCamelCase
        - UserController
        - ChallengeService
    - lowerCamelCase 
        - creationDate
        - validUUID

#### LINKS
[Google Java Style Guide: 5.3 Camel case defined](https://google.github.io/styleguide/javaguide.html#s5.3-camel-case)

----------------------------------------------------------------
# 5. LIBRARIES USED

## 5.1 Temp

----------------------------------------------------------------

# 6. REQUIRED PROGRAMS
Los siguientes programas son requeridos para poder trabajar en el proyecto:

## 6.1 Required programs

- **MongoDB**: MongoDB es una base de datos NoSQL ampliamente utilizada en el desarrollo de aplicaciones. Se utiliza para almacenar y recuperar datos de forma eficiente. Puedes descargar [MongoCompass](https://www.mongodb.com/try/download/compass) desde la web oficial.

- **Postman**: Postman es una herramienta que te permitirá probar y documentar las API de forma sencilla. Es especialmente útil para enviar solicitudes HTTP y verificar las respuestas. Puedes descargar [Postman](https://www.postman.com/downloads/) desde la web oficial.

- **Consul**: Consul es una herramienta de descubrimiento y configuración de servicios. Se utiliza para gestionar la comunicación entre diferentes componentes de la aplicación. Puedes descargar [Consul](https://developer.hashicorp.com/consul/downloads) desde la web oficial.

- **Docker**: Docker es una plataforma que permite empaquetar y distribuir aplicaciones en contenedores. Proporciona un entorno aislado para ejecutar la aplicación y sus dependencias. Puedes descargar [Docker](https://www.docker.com/products/docker-desktop/) desde la web oficial.

- **Git**: Git es un sistema de control de versiones distribuido ampliamente utilizado en el desarrollo de software. Te permitirá colaborar con otros desarrolladores y mantener un historial de cambios en el código fuente. Puedes descargar [Git](https://git-scm.com/downloads) desde la web oficial.

Es importante asegurarse de tener todas estas herramientas instaladas y configuradas correctamente antes de comenzar a trabajar en el proyecto.

----------------------------------------------------------------
# 7 RECOMMENDED PROGRAMS

## 7.1 Temp
