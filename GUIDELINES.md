# IT-Challenge Style Guide

## Table of Contents

1. [**PROJECT'S LINKS**](#1-projec-s-links)\
   1.1 [Project's Links](#11-project-links) 

2. [**WORK PROCEDURES**](#2-work-procedures)

   2.1 [First things to do in the project](#21-first-things-to-do-in-the-project)\
   2.2 [Daily Procedure](#22-daily-procedure)\
   2.3 [Work with cards Procedure](#23-work-with-cards-procedure)\
   2.4 [Points to consider when doing a PR](#24-points-to-consider-when-doing-a-pr)\
   2.5 [Scrum metodology](#25-scrum-metodology)

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
   5.1 [Main Plugins](#51-main-plugins) 
   5.2 [Implementation area](#52-implementation-area)
   5.3 [Testimplementation area](#53-testimplementation-area)

6. [**REQUIRED PROGRAMS**](#6-required-programs)

   6.1 [Requierd programs](#61-required-programs)

7. [**RECOMMENDED PROGRAMS**](#7-recommended-programs)

   7.1 [Recommended programs](#71-recommended-programs)

----------------------------------------------------------------

# 1. PROJECT'S LINKS
## 1.1 Project links
- GITHUB [link](https://github.com/IT-Academy-BCN/ita-challenges-backend)\
<img src="img/GitHub.jpg" alt="isolated" width="400"/>
- Backend Sprint Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1)\
<img src="img/Spring_BackLog.jpg" alt="isolated" width="400"/>
- Product Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D)\
<img src="img/Product BackLog.jpg" alt="isolated" width="400"/>
- Figma [link](https://www.figma.com/file/ScWpDKxEB3wEGbztXMSJO3/Projectes-IT-Academy?type=design&node-id=559-2230&mode=design)\
<img src="img/Figma.jpg" alt="Figma image" width="400"/>

----------------------------------------------------------------

# 2. WORK PROCEDURES

## 2.1 First things to do in the project
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

----------------------------------------------------------------
----------------------------------------------------------------
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

----------------------------------------------------------------
----------------------------------------------------------------
## 2.2 Daily Procedure
1. Conectarse a Teams a las 9:15h
2. Hacer pull de la rama "develop"
3. Hacer merge de develop con la rama en la que estás trabajando
4. Coffee time a las 10:15h aproximadamente

IMPORTANTE: Se espera que en proyecto estemos online con cámara encendida desde las 9:15h hasta las 13:15h, para trabajar en equipo (salvo circunstancias que lo justifiquen).

----------------------------------------------------------------
----------------------------------------------------------------
## 2.3 Work with cards Procedure
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

----------------------------------------------------------------
----------------------------------------------------------------
## 2.4 Points to consider when doing a PR
1. En la página de creación de a pull request, selecciona la rama base y la rama comparada:
   - La rama base es la rama a la que deseas fusionar tus cambios. En este caso es la rama "develop".
   - La rama comparada es la rama que contiene tus cambios.
2. Proporciona una descripción de los cambios que has realizado. Sé claro y conciso.
3. Cuando hayas terminado de completar la información de la pull request, haz clic en el botón verde "Create pull request" para crearla.
4. A continuación verás como se hace un análisis del build. Si el análisis pasa con éxito, se mostrará un tick verde en el resultado. Por el contrario, si detecta problemas en el código, se mostrará una aspa roja. En este caso, aprieta en "Details" y observa donde se encuentra el problema.
5. SonarCloud también va a analizar tu código. Te proporcionará información sobre los bugs, vulnerabilities, security hotspots y code smell. El converage debe ser igual o superior al 80.0%.
6. En caso de que debas revisar y corregir algunos problemas, actualiza tu rama local con los cambios y haz push nuevamente a la rama remota correspondiente.
7. La pull request se actualizará automáticamente con los nuevos cambios realizados en tu rama.

----------------------------------------------------------------
----------------------------------------------------------------
## 2.5 Scrum metodology
https://scrumguides.org/


<img src="img/VER5-scrum-framework_2020.jpg" alt="isolated" width="400"/>

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

## 5.1 Main Plugins
   - 'java'
     - 'org.springframework.boot' version '3.0.6'
   - 'jacoco'
     - 'org.sonarqube' version '4.0.0.2929'

## 5.2 Implementation area
    
   - BOOTSTRAP
     - Version 4.0.2
     - group: 'org.springframework.cloud', name: 'spring-cloud-starter-bootstrap', version: '4.0.2'
  
   - COMMONS IO
     - Version 2.11.0
     - 'commons-io:commons-io:2.11.0'
   - COMMONS LANG
     - Version 3.12.0
     - 'org.apache.commons:commons-lang3:3.12.0'
   - COMMONS VALIDATOR
     - Version 1.7
     - 'commons-validator:commons-validator:1.7'
   - CONSUL CONFIG
     - Version 4.0.2 
     - 'org.springframework.cloud:spring-cloud-starter-consul-config:4.0.2'
   - CONSUL DISCOVERY
     - Version 4.0.2
     - 'org.springframework.cloud:spring-cloud-starter-consul-discovery:4.0.2'
   - PROJECTLOMBOK
     - Version 1.18.26
     - 'org.projectlombok:lombok:1.18.26'
   - SPRING CONTEXT
     - Version 3
     - 'org.springframework:spring-context:5.3.13' 
   - SPRING BOOT AUTOCONFIGURE
        -  Version 3.0.6
        - 'org.springframework.boot:spring-boot-autoconfigure:3.0.6'
   - SPRING BOOT CLOUD COMMONS
      - Version 4.0.1
     - 'org.springframework.cloud:spring-cloud-commons:4.0.1'
   - SPRING BOOT STARTER ACTUATOR
     - Version 3.0.6
     - 'org.springframework.boot:spring-boot-starter-actuator:3.0.6' 
   - SPRING BOOT STARTER DATA MONGODB
     - Version 3.0.6
     - 'org.springframework.boot:spring-boot-starter-data-mongodb:3.0.6'
   - SPRING BOOT STARTER DATA MONGODB REACTIVE
     - Version 3.0.6
     - 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.0.6'
   - SPRING BOOT STARTER WEB
     - Version 3.0.6
     - 'org.springframework.boot:spring-boot-starter-web:3.0.6'
   - SPRING BOOT STARTER WEBFLUX
     - Version 3.0.6
     - 'org.springframework.boot:spring-boot-starter-webflux:3.0.6'
   - SPRINGDOC OPENAPI
     - Version 2.1.0
     - 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'
   - sdf

## 5.3 Testimplementation area

   - ASSERTJ
     - Version 3.24.2
     - 'org.assertj:assertj-core:3.24.2'
   - SPRING
     - Version 5.3.13
     - 'org.springframework:spring-test:5.3.13'
   - HAMCREST
     - Version 2.2
     - 'org.hamcrest:hamcrest:2.2'
   - JUNIT
     - 'org.junit.jupiter:junit-jupiter'
   - JUNIT JUPITER
     - Version 1.17.6 
     - 'org.testcontainers:junit-jupiter:1.17.6'
   - JUNIT PLATAFORM SUITE
     - Version 1.8.1
     - 'org.junit.platform:junit-platform-suite-engine:1.8.1'
   - MOCKITO
     - Version 5.3.1
     - 'org.mockito:mockito-core:5.3.1'
   - MOCK WEBSERVER
     - Version 4.9.3
     - 'com.squareup.okhttp3:mockwebserver:4.9.3'
   - MONGODB
     - Version 1.17.6
     - 'org.testcontainers:mongodb:1.17.6'
   - PROJECT REACTOR
     - Version 3.1.0 Release
     - 'io.projectreactor:reactor-test:3.1.0.RELEASE'
   - SPRING BOOT STARTER TEST
     - Version 3.06
     - 'org.springframework.boot:spring-boot-starter-test:3.0.6'
   - 

----------------------------------------------------------------

# 6. REQUIRED PROGRAMS
Los siguientes programas son requeridos para poder trabajar en el proyecto:

## 6.1 Required programs

- **MongoDB**: MongoDB es una base de datos NoSQL ampliamente utilizada en el desarrollo de aplicaciones. Se utiliza para almacenar y recuperar datos de forma eficiente. Puedes descargar [MongoCompass](https://www.mongodb.com/try/download/compass) desde la web oficial.

- **Postman**: Postman es una herramienta que te permitirá probar y documentar las API de forma sencilla. Es especialmente útil para enviar solicitudes HTTP y verificar las respuestas. Puedes descargar [Postman](https://www.postman.com/downloads/) desde la web oficial.

- **Consul**: Consul es una herramienta de descubrimiento y configuración de servicios. Se utiliza para gestionar la comunicación entre diferentes componentes de la aplicación. Puedes descargar [Consul](https://developer.hashicorp.com/consul/downloads) desde la web oficial.

- **Docker**: Docker es una plataforma que permite empaquetar y distribuir aplicaciones en contenedores. Proporciona un entorno aislado para ejecutar la aplicación y sus dependencias. Puedes descargar [Docker](https://www.docker.com/products/docker-desktop/) desde la web oficial.

- **Git**: Git es un sistema de control de versiones distribuido ampliamente utilizado en el desarrollo de software. Te permitirá colaborar con otros desarrolladores y mantener un historial de cambios en el código fuente. Puedes descargar [Git](https://git-scm.com/downloads) desde la web oficial.

- **Java SE Development Kit 17.0.7**: Java SE Development Kit (JDK) es un conjunto de herramientas necesarias para desarrollar aplicaciones en Java. Asegúrate de tener instalada la versión 17.0.7 del JDK que es la que se usa en este proyecto. Puedes descargar [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) desde la web oficial de Oracle.

Es importante asegurarse de tener todas estas herramientas instaladas y configuradas correctamente antes de comenzar a trabajar en el proyecto.

----------------------------------------------------------------
# 7 RECOMMENDED PROGRAMS

Los siguientes programas son recomendables para facilitar el trabajo en el proyecto:

## 7.1 Recommended programs

- **Mongo Tools**: Mongo Tools es un conjunto de utilidades de línea de comandos para trabajar con MongoDB. Proporciona herramientas adicionales para importar y exportar datos, entre otras tareas. Puedes descargar [MongoDB Command Line Database Tools](https://www.mongodb.com/try/download/database-tools) desde la web oficial.

- **Mongo Shell**: Mongo Shell es una interfaz de línea de comandos para MongoDB. Proporciona una forma interactiva de interactuar con la base de datos, ejecutar consultas y administrar colecciones. Puedes descargar [MongoDB Shell](https://www.mongodb.com/try/download/shell) desde la web oficial.

- **IntelliJ IDEA**: IntelliJ IDEA es un entorno de desarrollo integrado (IDE) muy utilizado en el desarrollo de aplicaciones Java y otros lenguajes de programación. Puedes descargar [IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/download/?section=windows) desde la web oficial.

- **Plugin SonarLint para IntelliJ IDEA**: SonarLint es una herramienta de análisis estático de código que te ayudará a identificar y corregir problemas de calidad en tu código. Es una ayuda útil para detectar Code Smell. Puedes obtener más información de [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) desde la web ofical de Jetbrains.

Recuerda que estos programas o plugins son recomendados, pero no son obligatorios. Utilizarlos puede mejorar tu productividad y la calidad del código, pero puedes optar por otras alternativas según tus preferencias y necesidades.
