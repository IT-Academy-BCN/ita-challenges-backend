# Descripción Técnica: Autenticación OAuth de GitHub para Múltiples Entornos

## 1. Planteamiento del Problema

Actualmente, el sistema de autenticación OAuth de GitHub está configurado para funcionar solo en el entorno de desarrollo desplegado. Al intentar iniciar sesión desde un frontend que se ejecuta en localhost, el flujo de autenticación falla debido a una discrepancia entre la URI de redirección utilizada y la registrada en GitHub.

Esto genera errores como bad_verification_code cuando el backend intenta intercambiar el código recibido por un token de acceso, ya que GitHub exige una coincidencia estricta de la URI de redirección.

Aunque hay dos aplicaciones OAuth separadas registradas en GitHub (una para localhost y otra para el entorno develop), el microservicio de autenticación (auth) solo esta configurado con las credenciales de una de ellas. Esto hace que el sistema sea incapaz de manejar la autenticación en múltiples entornos.

## 2. Objetivo del cambio

Habilitar el sistema de autenticación OAuth de GitHub para que funcione sin problemas tanto en localhost como en el entorno desplegado develop, gestionando dinámicamente las credenciales y las URI de redirección según el entorno activo.
### Objetivos Clave:

* Añadir soporte para múltiples configuraciones de clientes OAuth.
* Leer las propiedades de redirección y las credenciales desde archivos de configuración específicos por entorno (`application-*.yml`).
* Personalizar el flujo de autenticación para controlar dinámicamente el `redirect_uri`.

## Flujo de Autenticación GitHub OAuth

El flujo de autenticación está montado de la siguiente manera:

1. El frontend inicia el proceso de autenticación redirigiendo al usuario a GitHub para solicitar permiso.
2. GitHub, tras la autorización, devuelve un `code` al frontend.
3. El frontend envía este `code` al backend para su validación.
4. El backend recibe el `code` y lo transforma a un `token de acceso`.
5. El backend devuelve el token al front


## 3. Cambios de configuración

### 3.1. Configuración basada en perfiles

Dividir la configuración usando perfiles de Spring en tres archivos:

#### application.yml (común)

```yaml
spring:
  application:
    name: itachallenge-auth
    version: 2.0.0-RELEASE
  profiles:
    active: local
  security:
    oauth2:
      client:
        registration:
          github:
            environments:
              local:
                client-id: ${GITHUB_CLIENT_ID}
                client-secret: ${GITHUB_CLIENT_SECRET}
                redirect-uri: http://localhost:4200/ita-challenge/challenges
              dev:
                client-id: ${GITHUB_CLIENT_ID}
                client-secret: ${GITHUB_CLIENT_SECRET}
                redirect-uri: https://dev.ita-challenges.eurecatacademy.org/ita-challenge/challenges
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
            user-name-attribute: login
```

#### application-dev.yml

```yaml
server:
  port: 8761
uri_validate_token: https://dev.sso.itawiki.eurecatacademy.org/api/v1/tokens/validate
user:
  service:
    url: http://apisix-gateway:9080
```

#### application-local.yml

```yaml
server:
  port: 8080
uri_validate_token: http://localhost:8081/api/v1/tokens/validate
user:
  service:
    url: http://localhost:8764
```

### 3.2. Configuración dinámica de OAuth2

* Los distintos entornos (`local`, `dev`) ahora tienen valores específicos para `clientId`, `clientSecret` y `redirectUri`.
* Las credenciales están externalizadas usando variables de entorno por motivos de seguridad.

### 3.3. Perfil por defecto

El perfil `local` se establece como predeterminado para facilitar el desarrollo.

## 4. Implementación en backend

### 4.1. Class: `ClientConfig.java`

Almacena las propiedades necesarias de OAuth.

```java
@Data
public class ClientConfig {
   private String clientId;
   private String clientSecret;
   private String redirectUri;
}
```

### 4.2. Class: `GithubClientProperties.java`

Mapea las configuraciones de los entornos.

```java
@Data
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.github")
public class GithubClientProperties {
   private Map<String, ClientConfig> environments;

   public ClientConfig getClientConfig(String env) {
       return environments.get(env);
   }
}
```

## 5. Capa de servicio: `AuthService.java`

### Cambio en la firma del método

```java
Mono<String> exchangeCodeForToken(String code, String redirectUri);
```

### Método para definir el entorno

```java
private String determineEnvironment(String redirectUri) {
   if (redirectUri.contains("localhost")) {
       return "local";
   } else if (redirectUri.contains("dev.ita-challenges.eurecatacademy.org")) {
       return "dev";
   } else {
       throw new IllegalArgumentException("Unknown environment for redirect URI: " + redirectUri);
   }
}
```

### Lógica actualizada

```java
public Mono<String> exchangeCodeForToken(String code, String redirectUri) {
    String env = determineEnvironment(redirectUri);
    ClientConfig clientConfig = githubClientProperties.getEnvironments().get(env);

    if (clientConfig == null) {
        return Mono.error(new IllegalStateException("No client config found for environment: " + env));
    }

    WebClient webClient = webClientBuilder.build();

    Map<String, String> requestBody = createRequestBody(
            clientConfig.getClientId(),
            clientConfig.getClientSecret(),
            code,
            clientConfig.getRedirectUri()
    );

    return webClient
            .post()
            .uri(githubTokenUri)
            .header("Accept", "application/json")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(this::processTokenResponse)
            .onErrorResume(this::handleTokenError);
}
```

## 6. Capa del Controlador: Punto de entrada de Autenticación

### Situación Anterior:

* Solo se recibía el `code` en la solicitud.

### Nuevos Cambios:

* El controlador ahora espera tanto `code` como `redirect_uri`.
* Valida ambos parámetros.

### Métodos actualizados

```java
@PostMapping("/github/authenticate")
public Mono<ResponseEntity<Map<String, Object>>> authenticateWithGithub(@RequestBody Map<String, String> codeRequest) {
   String code = codeRequest.get("code");
   String redirectUri = codeRequest.get("redirect_uri");

   if (code == null || redirectUri == null) {
       return Mono.just(ResponseEntity.badRequest().body(Map.of(
               "message", "Missing 'code' or 'redirectUri' in the request",
               "isValid", false,
               "username", null
       )));
   }

   if (!(authService instanceof AuthService concreteAuthService)) {
       return Mono.error(new IllegalStateException("authService is not an instance of AuthService"));
   }

   return concreteAuthService.exchangeCodeForToken(code, redirectUri)
           .flatMap(authService::validateTokenWithGithub)
           .flatMap(response -> {
               if (!(boolean) response.get("isValid")) {
                   return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                           .header("X-Authentication-Status", "Failed")
                           .body(response));
               }
               String githubUsername = (String) response.get("username");
               return getUserDetailsFromGithubUsername(response, githubUsername);
           })
           .onErrorResume(ex -> {
               log.error("GitHub authentication error: {}", ex.getMessage());

               Map<String, Object> errorResponse = new HashMap<>();
               errorResponse.put("isValid", false);
               errorResponse.put("username", null);

               return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                       .header("X-Authentication-Status", "Error")
                       .header("X-Error-Message", "An error occurred during authentication.")
                       .body(errorResponse));
           });
}
```

## 7. Resultado

Con estos cambios, el sistema de autenticación:

* Selecciona dinámicamente las credenciales y las URIs de redirección según el entorno.
* Funciona correctamente tanto en `localhost` como en el entorno `develop`.
* Mejora el mantenimiento y la seguridad utilizando configuración basada en perfiles y secretos externalizados.


---
# Guía para implementar autenticación OAuth GitHub con múltiples entornos desde cero

## 1. Registrar aplicaciones OAuth en GitHub (Este paso ya está hecho)

- Crear una aplicación OAuth en GitHub para cada entorno:

    - **Local**: con redirect URI `http://localhost:4200/ita-challenge/challenges`

    - **Dev**: con redirect URI `https://dev.ita-challenges.eurecatacademy.org/ita-challenge/challenges`

- Obtener para cada aplicación el `client_id` y `client_secret`.


## 2. Configurar backend para gestionar múltiples entornos

### 2.1. Variables de configuración por entorno

- Crear archivos de configuración Spring Boot para cada entorno (ver el ejemplo de configuración de .yml arriba) :

    - `application-local.yml`

    - `application-dev.yml`

- Usar variables de entorno para valores sensibles, no hardcodear secrets.

## 2.2. Crear clases de configuración para mapear clientes

- Definir una clase POJO para almacenar `clientId`, `clientSecret`, `redirectUri`.

- Definir una clase que mapee estas configuraciones para cada entorno.

## 2.3. Lógica para determinar entorno según redirectUri

En el servicio de autenticación, implementar método para decidir qué configuración usar.

## 2.4. Método para intercambiar código por token

- Recibe `code` y `redirect_uri` desde frontend.

- Según `redirect_uri`, obtiene la configuración del cliente correspondiente.

- Hace llamada a GitHub para obtener el token usando `client_id`, `client_secret` y `redirect_uri` correctos.

### 2.5. Controlador backend

- Define un endpoint para autenticación que reciba un JSON con `code` y `redirect_uri`.
- Valida los parámetros y llama al servicio que intercambia el código por el token.

### 3. Pruebas del flujo OAuth sin levantar el frontend

Método para verificar que el backend gestione correctamente el intercambio del `code` por el `access_token`.

#### 1. Generar el `code` manualmente

* Hacer una peticion desde navegador (firefox, google..) usando el siguiente URL con los parámetros necesarios. Ejemplo:

    ```
     https://github.com/login/oauth/authorize?client_id=TU-CLIENT-ID&redirect_uri=http://localhost:4200/ita-challenge/challenges
    ```
_Ajustar URL según tu entorno y cambia el valor de client_id correspondiente al entorno, redirect_uri debe coincidir exactamente al uri definido en github por entorno_

* GitHub redirigirá a la URL indicada (aunque no esté disponible), incluyendo un parámetro `code` en la URL. Ejemplo:

    ```
    http://localhost:4200/ita-challenge/challenges?code=abc123
    ```

* Copiar el valor del `code` que aparece en la URL.

#### 2. Hacer la petición al backend usando Postman

* Preparar una petición `POST` al endpoint de autenticación del backend. Por ejemplo:

    ```
    POST http://localhost:8761/itachallenge/api/v1/auth/github/authenticate
    ```
_Ajustar URL según entorno y/o endpoint_

Usar el siguiente cuerpo en formato JSON:

    ```json
    {
      "code": "abc123",
      "redirect_uri": "http://localhost:4200/ita-challenge/challenges"
    }
    ```
_**P.D.** Asegúrate de ajustar la URL de la petición y el cuerpo JSON (`redirect_uri`, `code`) según el entorno que estés probando._

* Envíar la petición. El backend deberá:

    - Detectar el entorno (`localhost` vs `dev`) a partir del `redirect_uri`.
    - Obtener la configuración OAuth correspondiente.
    - Hacer la llamada a GitHub para intercambiar el `code` por un `access_token`.

#### Resultado esperado

- Si todo está configurado correctamente, el backend responderá con un token y los datos del usuario autenticado.
- Este método permite validar que la lógica del backend funciona antes de integrar con el frontend.

---

**Nota:** Asegúrarse de que el `redirect_uri` usado en la petición sea exactamente igual al registrado en la app OAuth de GitHub para ese entorno.

#### 4. Mejoras y buenas prácticas

- **Centralizar la configuración**: Usar un servicio o clase de configuración común si se gestionan múltiples entornos (local, develop, producción), para mantener coherencia y facilidad de mantenimiento.

- **Manejo adecuado de errores**: Implementar un sistema robusto para manejar errores.

- **Logging y monitoreo**: Añadir logs significativos en el backend (por ejemplo, cuando se recibe un `code`, al fallar una llamada a GitHub, etc.).

- **Impacto en tests existentes**: Estos cambios probablemente afecten los tests ya implementados. Es necesario revisar y adaptar los métodos, especialmente si ahora se pasa el `redirect_uri` como parámetro o se usa una lógica para mapear entornos.

- **Evita copiar directamente los ejemplos**: Los fragmentos de código incluidos en esta guía tienen fines ilustrativos. Deben ser **ajustados y refactorizados** según la estructura y convenciones del proyecto real.







