# Normalización de URL's

En este proyecto, es importante seguir ciertas convenciones al establecer la estructura y nomenclatura de las URL's utilizadas en el backend.

## Convenciones

A continuación, se presentan las convenciones que deben seguirse al normalizar las URL's:

- **Pluralización**: Se recomienda utilizar nombres plurales para los recursos en las URL's. Por ejemplo, en lugar de "/challenge", se utilizaría "/challenges".

- **Uso de sustantivos**: Se deben utilizar sustantivos en lugar de verbos en los nombres de los recursos para mantener una estructura coherente. Por ejemplo, "/challenges" en lugar de "/obtener-challenges".

- **Separadores**: Se ha establecido el uso de guiones ("-") como separador para las palabras en las URL's. Por ejemplo, "/bcn-zones"

- **Consistencia en mayúsculas y minúsculas**: Se debe decidir si las URL's deben seguir una convención de mayúsculas o minúsculas. Por ejemplo, "/challenges" en minúsculas o "/Challenges" en mayúsculas.

- **Evitar caracteres especiales**: Se deben evitar los caracteres especiales en las URL's y, en su lugar, utilizar caracteres alfanuméricos.

- **Orden jerárquico**: Si hay una jerarquía en los recursos, se debe reflejar en la estructura de las URL's. Por ejemplo, "/challenges/550e8400-e29b-41d4-a716-446655440000/update" para obtener los desafios del usuario con ID 550e8400-e29b-41d4-a716-446655440000.

- **Versionado**: Si se requiere versionar la API, se debe considerar incluir la versión en la URL. Por ejemplo, "/v1/challenges" para la versión 1 de la API de challenges.

- **Evitar verbos en URL's**: En general, se recomienda evitar incluir verbos en las URL's y utilizar los métodos HTTP adecuados para realizar acciones en los recursos.

- **Consistencia con nombres de atributos**: Los nombres de atributos utilizados en las URL's deben ser coherentes con los nombres utilizados en el modelo de datos.

- **Evitar URL's demasiado largas**: Se debe evitar el uso de URL's excesivamente largas y buscar mantenerlas concisas y significativas.


## Ejemplos

Aquí se presentan algunos ejemplos de cómo deben estructurarse las URL's según las convenciones establecidas:

- `/challenges` - Obtener una lista de todos los challenges.
- `/challenges/550e8400-e29b-41d4-a716-446655440000` - Obtener los detalles del challenge con ID 550e8400-e29b-41d4-a716-446655440000.
- `/challenges/550e8400-e29b-41d4-a716-446655440000/update` - Editar los detalles del challenge con ID 550e8400-e29b-41d4-a716-446655440000.

## Referencias

- [RestfulAPI.net - Resource Naming](https://restfulapi.net/resource-naming/)


