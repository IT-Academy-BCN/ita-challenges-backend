# IT-Challenge Style Guide

## Table of Contents

1. [**PROJECT'S LINKS**](#1-project's-links)

   1.1 [Temp](#11-temp)

2. [**WORK PROCEDURES**](#2-work-procedures)

   2.1 [Temp](#21-temp)

3. [**URL NORMALIZATION**](#3-url-normalization)

   3.1 [Convenciones](#31-convenciones)

4. [**DEFINITIONS OF METHOD, CLASS, ETC...**](#4-definitions-of-method,-class,-etc...)

   4.1 [Temp](#41-temp)

5. [**LIBRARIES USED**](#5-libraries-used)

   5.1 [Temp](#51-temp)

6. [**REQUIRED PROGRAMS**](#6-required-programs)

   6.1 [Temp](#61-temp)

7. [**RECOMMENDED PROGRAMS**](#7-recommended-programs)

   7.1 [Temp](#71-temp)



# 1. PROJECT'S LINKS

## 1.1 Temp

# 2. WORK PROCEDURES

## 2.1 Temp

# 3. URL NORMALIZATION

En este proyecto, es importante seguir ciertas convenciones al establecer la estructura y nomenclatura de las URL's utilizadas en el backend.

## 3.1 Convenciones

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

# 4. DEFINITIONS OF METHOD, CLASS, ETC...

## 4.1 Temp

# 5. LIBRARIES USED

## 5.1 Temp

# 6. REQUIRED PROGRAMS

## 6.1 Temp

# 7 RECOMMENDED PROGRAMS

## 7.1 Temp