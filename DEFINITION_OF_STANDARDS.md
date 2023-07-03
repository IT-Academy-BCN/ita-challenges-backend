# Normalización de URL's

En este proyecto, es importante seguir ciertas convenciones al establecer la estructura y nomenclatura de las URL's utilizadas en el backend.

## Convenciones

A continuación, se presentan las convenciones que deben seguirse al normalizar las URL's:

- **Pluralización**: Se recomienda utilizar nombres plurales para los recursos en las URL's. Por ejemplo, en lugar de "/challenge", se utilizaría "/challenges".

- **Uso de sustantivos**: Se deben utilizar sustantivos en lugar de verbos en los nombres de los recursos para mantener una estructura coherente. Por ejemplo, "/challenges" en lugar de "/obtener-challenges".

- **Separadores**: Se ha establecido el uso de guiones bajos ("_") como separador para las palabras en las URL's. Por ejemplo, "/challenges/550e8400-e29b-41d4-a716-446655440000" o "/usuarios/550e8400-e29b-41d4-a716-446655440000/editar".

## Ejemplos

Aquí se presentan algunos ejemplos de cómo deben estructurarse las URL's según las convenciones establecidas:

- `/challenges` - Obtener una lista de todos los usuarios.
- `/usuarios/550e8400-e29b-41d4-a716-446655440000` - Obtener los detalles del usuario con ID 550e8400-e29b-41d4-a716-446655440000.
- `/usuarios/550e8400-e29b-41d4-a716-446655440000/editar` - Editar los detalles del usuario con ID 550e8400-e29b-41d4-a716-446655440000.

## Referencias

- [RestfulAPI.net - Resource Naming](https://restfulapi.net/resource-naming/)


