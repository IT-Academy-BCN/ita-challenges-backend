# Guia d'estil de IT-Challenge

## Taula de continguts

1. [**ENLLAÇOS DEL PROJECTE**](#1-enllaços-del-projecte)\
   1.1 [Enllaços del projecte](#11-enllaços-del-projecte)

2. [**PROCEDIMENTS DE TREBALL**](#2-procediments-de-treball)\
   2.1 [Primeres tasques del projecte](#21-primeres-tasques-del-projecte)\
   2.2 [Configuracions de Git](#22-configuracions-git)\
   2.3 [Procediment diari](#23-procediment-diari)\
   2.4 [Procediment de treball amb targetes](#24-treball-amb-les-targetes)\
   2.5 [Aspectes a tenir en compte en fer una PR](#25-aspectes-a-tenir-en-compte-a-lhora-de-fer-un-pr)\
   2.6 [Metodologia Scrum](#26-metodologia-scrum)

3. [**NORMALITZACIÓ D'URL**](#3-normalització-durl)\
   3.1 [Convencions](#31-convencions)

4. [**DEFINICIONS DE MÈTODE, CLASSE, ETC...**](#4-definicions-de-mètode-classe-etc)\
   4.1 [Noms dels Packages](#41-noms-dels-packages)\
   4.2 [Noms de les classes](#42-noms-de-les-classes)\
   4.2.1 [Noms de les classes de prova (testing)](#421-noms-de-les-classes-de-prova--testing-)\
   4.2.2 [Noms de les classes d'interficie](#422-noms-de-les-classes-dinterfice)\
   4.3 [Noms dels mètodes](#43-noms-dels-mètodes)\
   4.3.1 [Noms dels mètodes de prova (testing)](#431-noms-dels-mètodes-de-prova--testing-)\
   4.4 [Noms de constants](#44-noms-de-constants)\
   4.5 [Noms de variables locals](#45-noms-de-variables-locals)\
   4.5.1 [Variables temporals "throwaway"](#451-variables-temporals--throwaway-)\
   4.6 [Noms de variables de tipus](#46-noms-de-variables-de-tipus)\
   4.7 [Camel case: definit](#47-camel-case--definit)

5. [**LLIBRERIES UTILITZADES**](#5-llibreries-utilitzades)\
   5.1 [Plugins principals](#51-plugins-principals)\
   5.2 [Àrea d'implementació](#52-àrea-dimplementació)\
   5.3 [Àrea de proves (testing)](#53-àrea-de-proves--testing-)

6. [**PROGRAMES REQUERITS**](#6-programes-requerits)\
   6.1 [Programes requerits](#61-programes-requerits)

7. [**PROGRAMAS RECOMANATS**](#7-programas-recomanats)\
   7.1 [Programas recomanats](#71-programes-recomanats)


----------------------------------------------------------------

# 1. ENLLAÇOS DEL PROJECTE
## 1.1 Enllaços del projecte
- GITHUB [link](https://github.com/IT-Academy-BCN/ita-challenges-backend)\
  <img src="img/GitHub.jpg" alt="isolated" width="400"/>
- Backend Sprint Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1)\
  <img src="img/Spring_BackLog.jpg" alt="isolated" width="400"/>
- Product Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D)\
  <img src="img/Product BackLog.jpg" alt="isolated" width="400"/>
- Figma [link](https://www.figma.com/file/ScWpDKxEB3wEGbztXMSJO3/Projectes-IT-Academy?type=design&node-id=559-2230&mode=design)\
  <img src="img/Figma.jpg" alt="Figma image" width="400"/>

----------------------------------------------------------------

# 2. PROCEDIMENTS DE TREBALL

## 2.1 Primeres tasques del projecte
### 1. Afegir el teu nom y GitHub al arxiu contributors.md

1. Clona el repositori ita-challenges-backend de GitHub al teu sistema local:

         git clone https://github.com/IT-Academy-BCN/ita-challenges-backend.git
2. Canvia el directori del repositori clonat:

         cd ita-challenges-backend
3. Assegureu-vos d'estar a la branca "develop". Podeu verificar les branques disponibles i la branca actual executant la següent ordre:

         git branch
4. Si no estàs a la branca "develop", canvieu-ho executant la següent ordre:

         git checkout develop
5. Creeu una nova branca amb el vostre nom per realitzar els vostres canvis:

         git checkout -b nombre-de-su-rama
   Reemplaça "nom-de-la-branca" amb un nom descriptiu que indiqui els canvis que planeja realitzar.


6. Obriu el fitxer contributors.md i afegiu-ne el nom i el GitHub.


7. Després de fer un git add y un git commit, realitzi el següent git push:

         git push origin nombre-de-su-rama
8. Obriu el repositori a GitHub i hauríeu de veure un missatge que us permet crear un "pull request" des de la seva branca acabada de crear a la branca "develop". Feu clic a l'enllaç per crear el pull request.

----------------------------------------------------------------
----------------------------------------------------------------
### 2. Importar data a MongoDB (Exemple per a data del micro itachallenge-challenge)

1. Assegureu-vos de tenir les MongoDB Tools instal·lades. Si encara no en tens, vés a l'apartat de “Programes necessaris” i segueix les instruccions proporcionades per descarregar-les al sistema operatiu.


2. Ara que tens les MongoDB Tools instal·lades, obre la terminal de MongoDB executant la següent ordre:

          mongosh

3. Fes servir la següent ordre per crear la base de dades "challenges":

          use challenges

4. Un cop dins de la base de dades "challenges", enganxa el següent bloc de codi:

         db.createUser({
         user: "admin_challenge",
         pwd: "BYBcMJEEWw5egRUo",
         roles: [
         { role: "dbOwner", db: "challenges" }
         ]
         });

5. Executeu la següent ordre per veure si l'usuari s'ha creat correctament:

          show users
6. Surt de la terminal de MongoDB escrivint la següent ordre:

          exit

7. Mou el fitxer load-data de la carpeta monogdb.init a la carpeta mongodb-test-data.
   Si utilitzeu Windows, utilitzeu el fitxer .bat, en canvi, si utilitzeu Mac o Linux, el fitxer és .sh.


8. A la terminal normal, navega a la carpeta mongodb-test-data utilitzant l'ordre cd.


9. Per importar el document, executa la següent ordre:

   · A Windows:

          load-data.bat

   · A Mac o Linux:

          ./load-data.sh

   Si rebeu un missatge de permís denegat, atorgueu permisos d'execució al fitxer amb l'ordre següent:

          chmod +x load-data.sh

10. Després, moveu el fitxer load-data.bat o load-data.sh de tornada a la vostra carpeta original, mongodb.init.


11. Obre MongoDB Compass, fes un "Reload Data" i ja veuràs la base de dades correctament importada.

----------------------------------------------------------------
----------------------------------------------------------------
## 2.2 Configuracions Git

Configuracions necessàries de Git per evitar problemes

### 2.2.1 Git ignore
1. Copieu el fitxer ".gitignore" del projecte arrel\
   <img src="img/Gitignore_doc.jpg" alt="Gitignore_doc.jpg" width="300"/>
2. Enganxeu-lo al directori de l'ordinador (fora de la carpeta del projecte)
3. A Git Bash...
4. Executeu l'ordre: 'git config --global core-excludesfile path/.gitignore'\
   Where...\
   'Path': és el camí de la carpeta on es troba el fitxer ".gitignore".\
   Nota! Aquesta configuració no només servirà per al projecte actual, sinó també per a tots els projectes que feu amb GIT.

### 2.2.2 Autocrlf
Si teniu un problema amb un PR que modifica molts fitxers, >100, heu de fer el següent...
1. A Git Bash...
2. Executeu l'ordre: 'git config --global core.autocrlf true'


----------------------------------------------------------------
----------------------------------------------------------------
## 2.3 Procediment diari
1. Connectar-se a Teams a les 9:15h
2. Fer pull de la branca "develop"
3. Fer merge de develop amb la branca en què estàs treballant
4. "Coffee time" a les 10:15h aproximadament

IMPORTANT: S'espera que en projecte estiguem en línia amb càmera encesa des de les 9:15h fins a les 13:15h, per treballar en equip (tret de circumstàncies que ho justifiquin).

----------------------------------------------------------------
----------------------------------------------------------------
## 2.4 Treball amb les targetes
### Com assignar-se una targeta
1. Al tauler de Spring Backlog, localitza la targeta que vols assignar-te.
2. Fes clic a la targeta per obrir-la i veure'n més detalls.
3. A la part de la dreta, al camp "Assigness", si prems "Add assigness" s'obre un desplegable amb tots els participants del projecte i ja et pots seleccionar a tu per assignar-te-la.
4. Un cop t'has assignat la targeta, el vostre perfil d'usuari es mostrarà com el responsable de la targeta.

### Sistema de columnes per organitzar i visualitzar el progrés de les targetes
1. Et pots assignar qualsevol targeta que estigui a la columna "Todo" sempre que estigui lliure.
2. Quan comencis a treballar amb la targeta, l'hauràs de moure a la columna "Doing".
3. Quan la tasca de la targeta hagi estat completada i hagis fet la PR a la branca "develop", pots moure la targeta a la columna "Testing" per indicar que ja està a punt per ser provada.
4. Finalment, quan el teu PR hagi estat acceptat i, per tant, la targeta hagi estat aprovada a les proves, aquesta es passarà a la columna "Done". Això indica que la targeta ha estat completada amb èxit.

### Ja tinc una targeta assignada. Ara que faig?
1. Obre el teu terminal o línia d'ordres i navega fins al directori del teu projecte. Assegureu-vos d'estar a la branca "develop".


2. Crea una nova branca utilitzant el format "feature#numeroDeLaTarjeta". Es refereix al número de la targeta del Spring Backlog. Per exemple:

          git checkout -b feature#123
3. Ara pots començar a fer els canvis a la teva branca.


4. Un cop hagis realitzat les modificacions necessàries fes un push de la teva branca al repositori remot. Si estiguéssim a la feature#123 hauríem de fer:

          git push origin feature#123

5. Finalment, vés a GitHub on es troba el teu repositori i crea un "pull request" des de la teva branca "feature#123" cap a la branca "develop".

----------------------------------------------------------------
----------------------------------------------------------------
## 2.5 Aspectes a tenir en compte a l'hora de fer un PR
1. A la pàgina de creació de "pull request", selecciona la branca base i la branca comparada:
    - La branca base és la branca a la qual vols fusionar els teus canvis. En aquest cas és la branca “develop”.
    - La branca comparada és la branca que conté els teus canvis.
2. Proporciona una descripció dels canvis que has fet. Sigues clar i concís.
3. Quan hagis acabat de completar la informació de la pull request, fes clic al botó verd "Create pull request" per crear-la.
4. A continuació veuràs com es fa una anàlisi del "build". Si l'anàlisi passa amb èxit, es mostrarà un tick verd al resultat. Per contra, si detecta problemes al codi, es mostrarà una aspa vermella. En aquest cas, prem a "Details" i observa on es troba el problema.
5. SonarCloud també analitzarà el teu codi. Et proporcionarà informació sobre els bugs, vulnerabilities, security hotspots i code smell. El converage ha de ser igual o superior al 80,0%.
6. En cas que hagis de revisar i corregir alguns problemes, actualitza la teva branca local amb els canvis i fes push novament a la branca remota corresponent.
7. La pull request s'actualitzarà automàticament amb els nous canvis realitzats a la teva branca.

----------------------------------------------------------------
----------------------------------------------------------------
## 2.6 Metodologia Scrum
https://scrumguides.org/


<img src="img/VER5-scrum-framework_2020.jpg" alt="isolated" width="400"/>

----------------------------------------------------------------

# 3. NORMALITZACIÓ D'URL
En aquest projecte, és important seguir certes convencions en establir l'estructura i la nomenclatura de les URL's utilitzades en el backend.

## 3.1 Convencions

A continuació, es presenten les convencions que cal seguir en normalitzar les URL's:

- **Pluralització**: Es recomana utilitzar noms plurals per als recursos a les URL's. Per exemple, en lloc de "/challenge", s'utilitzaria "/challenges".

- **Ús de substantius**: Cal utilitzar substantius en lloc de verbs en els noms dels recursos per mantenir una estructura coherent. Per exemple, "/challenges" en lloc de "/obtenir-challenges".

- **Separadors**: S'ha establert l'ús de guions ("-") com a separador per a les paraules a les URL's. Per exemple, "/bcn-zones"

- **Consistència en minúscules**: Les URL's han de seguir una convenció de minúscules. Per exemple, "/challenges" en minúscules en lloc de "/Challenges" o "/CHALLENGES".

- **Evitar caràcters especials**: Cal evitar els caràcters especials a les URL's i, al seu lloc, utilitzar caràcters alfanumèrics.

- **Ordre jeràrquic**: Si hi ha una jerarquia als recursos, s'ha de reflectir en l'estructura de les URL's. Per exemple, "/challenges/{challengeId}/update" per obtenir els desafiaments de l'usuari.

- **Versionat**: Si cal versionar l'API, cal considerar incloure la versió a la URL. Per exemple, "/itachallenge/api/v1/challenge" per a la versió 1 de l'API de challenges.

- **Evitar verbs a URL's**: En general, es recomana evitar incloure verbs a les URL's i utilitzar els mètodes HTTP adequats per realitzar accions en els recursos.

- **Consistència amb noms d'atributs**: Els noms d'atributs utilitzats a les URL han de ser coherents amb els noms utilitzats en el model de dades.

- **Evitar URL's massa llargues**: S'ha d'evitar l'ús d'URLs excessivament llargues i cercar mantenir-les concises i significatives.


## Exemples

Aquí es presenten alguns exemples de com s'han d'estructurar les URL segons les convencions establertes:

- `/challenges` - Obtenir una llista de tots els challenges.
- `/challenges/{challengeId}` - Obtenir els detalls del challenge amb ID 550e8400-e29b-41d4-a716-446655440000.
- `/challenges/{challengeId}/update` - Editeu els detalls del challenge amb ID 550e8400-e29b-41d4-a716-446655440000.


----------------------------------------------------------------

# 4. DEFINICIONS DE MÈTODE, CLASSE, ETC...

## 4.1 Noms dels Packages
#### NORMES
    - Tot en minúscula
    - Només lletres i dígits

#### EXEMPLE
    - com.itachallenge.user
    - exception
    - helper

### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.1 package names](https://google.github.io/styleguide/javaguide.html#s5.2.1-package-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.2 Noms de les classes
#### NORMES
    - UpperCamelCase
    - Només lletres i dígits

#### EXEMPLE
    - UserController
    - PropertiesConfig

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.2 class names](https://google.github.io/styleguide/javaguide.html#s5.2.2-class-names)

### 4.2.1 Noms de les classes de prova (testing)
#### NORMES
    - UpperCamelCase
    - Final amb 'Test'
    - Només lletres i dígits

#### EXEMPLE
    - ChallengeControllerTest
    - ResourceHelperTest

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.2 class names](https://google.github.io/styleguide/javaguide.html#s5.2.2-class-names)

### 4.2.2 Noms de les classes d'interfice
#### NORMES
    - UpperCamelCase
    - Només lletres i dígits

----------------------------------------------------------------
----------------------------------------------------------------
## 4.3 Noms dels mètodes
#### NORMES
    - lowerCamelCase
    - Només lletres i dígits

#### EXEMPLE
    - isValidUUID
    - initReactorHttpClient

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.3 method names](https://google.github.io/styleguide/javaguide.html#s5.2.3-method-names)

## 4.3.1 Noms dels mètodes de prova (testing)
#### NORMES
    - lowerCamelCase
    - Només lletres i dígits
    - Final amb '_test'

#### EXEMPLE
    - getChallengeId_test
    - findAll_test

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.3 method names](https://google.github.io/styleguide/javaguide.html#s5.2.3-method-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.4 Noms de constants
#### NORMES
    - Majúscules
    - Només lletres i dígits
    - Paraula separada amb un sol guió baix '_'

#### EXEMPLE
    - static final int BEST_YEAR = 1977;
    - static final String BEST_MONTH = "February";

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.4 constant names](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.5 Noms de variables locals
#### NORMES
    - lowerCamelCase
    - Comença amb lletra
    - Els noms de variables han de ser curts però significatius  

#### EXEMPLE
    - int year = 1977;
    - static final String BEST_MONTH = "February";

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)\
[Google Java Style Guide: 5.2.4 constant names](https://google.github.io/styleguide/javaguide.html#s5.2.4-constant-names)

### 4.5.1 Variables temporals "throwaway"
#### NORMES
    - Utilice sólo para variables temporales "throwaway"
    - Minúscula

#### EXEMPLE
    - int   => i, j, k, m and n
    - char  => c, d and e

#### LINKS
[Oracle Code Conventions for java: 9 - Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.6 Noms de variables de tipus
#### NORMES
    - Una sola lletra majúscula, seguida opcionalment d'un sol número
    - Un nom en la forma utilitzada per a les classes

#### EXEMPLE
    - E, T, X, T2
    - RequestT, ChallengeT

#### LINKS
[Google Java Style Guide: 5.2.8 Type variable names](https://google.github.io/styleguide/javaguide.html#s5.2.8-type-variable-names)

----------------------------------------------------------------
----------------------------------------------------------------
## 4.7 Camel case: definit
#### NORMES
    - Només lletres i dígits
    - UpperCamelCase
        - Cada primera lletra de cada paraula és lletra majúscula
    - lowerCamelCase
        - La primera lletra de cada paraula està en minúscula, excepte la primera paraula que comença
           amb una lletra minúscula 

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
# 5. LLIBRERIES UTILITZADES

## 5.1 Plugins principals
- 'java'
    - 'org.springframework.boot' version '3.0.6'
- 'jacoco'
    - 'org.sonarqube' version '4.0.0.2929'

## 5.2 Àrea d'implementació

- BOOTSTRAP
    - Versió 4.0.2
    - 'org.springframework.cloud', name: 'spring-cloud-starter-bootstrap', version: '4.0.2'

- COMMONS IO
    - Versió 2.11.0
    - 'commons-io:commons-io:2.11.0'
- COMMONS LANG
    - Versió 3.12.0
    - 'org.apache.commons:commons-lang3:3.12.0'
- COMMONS VALIDATOR
    - Versió 1.7
    - 'commons-validator:commons-validator:1.7'
- CONSUL CONFIG
    - Versió 4.0.2
    - 'org.springframework.cloud:spring-cloud-starter-consul-config:4.0.2'
- CONSUL DISCOVERY
    - Versió 4.0.2
    - 'org.springframework.cloud:spring-cloud-starter-consul-discovery:4.0.2'
- PROJECTLOMBOK
    - Versió 1.18.26
    - 'org.projectlombok:lombok:1.18.26'
- SPRING CONTEXT
    - Versió 3
    - 'org.springframework:spring-context:5.3.13'
- SPRING BOOT AUTOCONFIGURE
    -  Versió 3.0.6
    - 'org.springframework.boot:spring-boot-autoconfigure:3.0.6'
- SPRING BOOT CLOUD COMMONS
    - Versió 4.0.1
    - 'org.springframework.cloud:spring-cloud-commons:4.0.1'
- SPRING BOOT STARTER ACTUATOR
    - Versió 3.0.6
    - 'org.springframework.boot:spring-boot-starter-actuator:3.0.6'
- SPRING BOOT STARTER DATA MONGODB
    - Versió 3.0.6
    - 'org.springframework.boot:spring-boot-starter-data-mongodb:3.0.6'
- SPRING BOOT STARTER DATA MONGODB REACTIVE
    - Versió 3.0.6
    - 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive:3.0.6'
- SPRING BOOT STARTER WEB
    - Versió 3.0.6
    - 'org.springframework.boot:spring-boot-starter-web:3.0.6'
- SPRING BOOT STARTER WEBFLUX
    - Versió 3.0.6
    - 'org.springframework.boot:spring-boot-starter-webflux:3.0.6'
- SPRINGDOC OPENAPI
    - Versió 2.1.0
    - 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'

## 5.3 Àrea de proves (testing)

- ASSERTJ
    - Versió 3.24.2
    - 'org.assertj:assertj-core:3.24.2'
- SPRING
    - Versió 5.3.13
    - 'org.springframework:spring-test:5.3.13'
- HAMCREST
    - Versió 2.2
    - 'org.hamcrest:hamcrest:2.2'
- JUNIT
    - 'org.junit.jupiter:junit-jupiter'
- JUNIT JUPITER
    - Versió 1.17.6
    - 'org.testcontainers:junit-jupiter:1.17.6'
- JUNIT PLATAFORM SUITE
    - Versió 1.8.1
    - 'org.junit.platform:junit-platform-suite-engine:1.8.1'
- MOCKITO
    - Versió 5.3.1
    - 'org.mockito:mockito-core:5.3.1'
- MOCK WEBSERVER
    - Versió 4.9.3
    - 'com.squareup.okhttp3:mockwebserver:4.9.3'
- MONGODB
    - Versió 1.17.6
    - 'org.testcontainers:mongodb:1.17.6'
- PROJECT REACTOR
    - Version 3.1.0 Release
    - 'io.projectreactor:reactor-test:3.1.0.RELEASE'
- SPRING BOOT STARTER TEST
    - Versió 3.06
    - 'org.springframework.boot:spring-boot-starter-test:3.0.6'

----------------------------------------------------------------

# 6. PROGRAMES REQUERITS
Els programes següents són requerits per poder treballar en el projecte:

## 6.1 Programes requerits

- **MongoDB**: MongoDB és una base de dades NoSQL àmpliament utilitzada en el desenvolupament d'aplicacions. S'utilitza per emmagatzemar i recuperar dades de manera eficient. Pots descarregar [MongoCompass](https://www.mongodb.com/try/download/compass) des de la web oficial.

- **Postman**: Postman és una eina que us permetrà provar i documentar les API de forma senzilla. És especialment útil per enviar sol·licituds HTTP i verificar les respostes. Pots descarregar [Postman](https://www.postman.com/downloads/) des de la web oficial.

- **Consul**: Consul és una eina de descobriment i configuració de serveis. Es fa servir per gestionar la comunicació entre diferents components de l'aplicació. Pots descarregar [Consul](https://developer.hashicorp.com/consul/downloads) des de la web oficial.

- **Docker**: Docker és una plataforma que permet empaquetar i distribuir aplicacions en contenidors. Proporciona un entorn aïllat per executar l'aplicació i les dependències. Pots descarregar [Docker](https://www.docker.com/products/docker-desktop/) des de la web oficial.

- **Git**: Git és un sistema de control de versions distribuït àmpliament utilitzat en el desenvolupament de programari. Et permetrà col·laborar amb altres desenvolupadors i mantenir un historial de canvis al codi font. Pots descarregar [Git](https://git-scm.com/downloads) des de la web oficial.

- **Java SE Development Kit 17.0.7**: Java SE Development Kit (JDK) és un conjunt d'eines necessàries per desenvolupar aplicacions en Java. Assegureu-vos de tenir instal·lada la versió 17.0.7 del JDK que és la que s'usa en aquest projecte. Podeu descarregar [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) des del web oficial d'Oracle.

És important assegurar-se de tenir totes aquestes eines instal·lades i configurades correctament abans de començar a treballar al projecte.

----------------------------------------------------------------
# 7 PROGRAMAS RECOMANATS

Els programes següents són recomanables per facilitar el treball en el projecte:

## 7.1 Programes recomanats

- **Mongo Tools**: Mongo Tools és un conjunt d'utilitats de línia d'ordres per treballar amb MongoDB. Proporciona eines addicionals per importar i exportar dades, entre d'altres tasques. Podeu descarregar [MongoDB Command Line Database Tools](https://www.mongodb.com/try/download/database-tools) des del web oficial.

- **Mongo Shell**: Mongo Shell és una interfície de línia d'ordres per a MongoDB. Proporciona una manera interactiva d'interactuar amb la base de dades, executar consultes i administrar col·leccions. Pots descarregar [MongoDB Shell](https://www.mongodb.com/try/download/shell) des de la web oficial.

- **IntelliJ IDEA**: IntelliJ IDEA és un entorn de desenvolupament integrat (IDE) molt utilitzat en el desenvolupament d'aplicacions Java i altres llenguatges de programació. Pots descarregar [IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/download/?section=windows) des de la web oficial.

- **Plugin SonarLint per a IntelliJ IDEA**: SonarLint és una eina d'anàlisi estàtica de codi que t'ajudarà a identificar i corregir problemes de qualitat al teu codi. És una ajuda útil per detectar Code Smell. Pots obtenir més informació de [SonarLint](https://plugins.jetbrains.com/plugin/7973-sonarlint) des del web ofical de Jetbrains.

Recordeu que aquests programes o plugins són recomanats, però no són obligatoris. Utilitzar-los pot millorar la teva productivitat i la qualitat del codi, però pots optar per altres alternatives segons les teves preferències i necessitats.
