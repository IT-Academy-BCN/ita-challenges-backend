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

   7.1 [Recommended programs](#71-recommended-programs)

----------------------------------------------------------------

# 1. PROJECT'S LINKS

## 1.1 Temp

----------------------------------------------------------------

# 2. WORK PROCEDURES

## 2.1 Welcome Procedure

## 2.2 Daily Procedure

## 2.3 Work with cards Procedure

## 2.4 Make a PR

## 2.5 Scrum metodology

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

## LINKS
[Code Conventions for the Java Programming Language](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

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
