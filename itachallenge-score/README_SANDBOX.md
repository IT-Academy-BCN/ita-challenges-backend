### Notas para el compilador

En el proyecto actual se usa Docker para la creación de un contenedor con el fin de ejecutar el proyecto de forma aislada. Actualmente se está utilizando una imagen "openjdk:21" para la ejecución del proyecto, en el futuro se cambiará por una imagen custom, capando librerías y módulos que no se necesiten para la ejecución del proyecto.
Antes de pasar el código a ejecución, se pasa por una serie de filtros utilizando el patrón de diseño Chain of Responsibility, para asegurar que el código pueda ser ejecutado de forma segura.
La comparación de los resultados ahora mismo está hardcodeada, en el futuro se tendrá que modificar añadiendo un repositorio con los resultados esperados para cada UUID de ejercicio y lenguaje.

## Chain of Responsibility

#### UnescapeFilter 
    Se encarga de deshacer el escape de caracteres que tiene que hacer Frontend para enviar el código al Backend.
#### ASCIIFilter
    Se encarga de verificar que el código solo contenga caracteres ASCII validos del 0 al 127.
#### HTMLSanitizeFilter
    Se encarga de sanitizar el código en busca de posibles inyecciones de código HTML.
#### keywordFilter
    Se encarga de verificar que el código no contenga palabras reservadas que puedan ser peligrosas para la ejecución del código.
#### SecurityFilter
    Se encarga de verificar que el código no contenga librerías o módulos que puedan ser peligrosos para la ejecución del código.


## DockerExecutor

La clase `DockerExecutor` es la encargada de levantar el contenedor y ejecutar el código del usuario. Esta clase tiene varias responsabilidades y utiliza varias técnicas para asegurar que los recursos se gestionen adecuadamente.

#### Variables Finales

- **DOCKER_IMAGE_NAME**: Imagen de Docker que se usará para la ejecución del proyecto.
- **CONTAINER**: Nombre del contenedor que se creará.
- **CODE_TEMPLATE**: Plantilla de código que se usará para la ejecución del proyecto.
  (Esta plantilla no se puede parametrizar debido a restricciones de compilación)
- **TIMEOUT_SECONDS**: Tiempo máximo de ejecución del código del usuario.

#### Variables de Configuración

- **windowsCommand**: Comando de shell para sistemas Windows, inyectado mediante `@Value`.
- **unixCommand**: Comando de shell para sistemas Unix, inyectado mediante `@Value`.
- **isWindows**: Booleano que indica si el sistema operativo es Windows.

#### Métodos

##### execute

Este método es el punto de entrada principal para ejecutar el código del usuario. Realiza los siguientes pasos:

       1. Validación del Código: Verifica si el código Java proporcionado es nulo.
       2. Formateo del Código: Formatea el código Java utilizando la plantilla `CODE_TEMPLATE`.
       3. Construcción del Comando: Construye el comando Docker para ejecutar el código.
       4. Limpieza de Contenedores: Llama al método `cleanUpContainers` para eliminar cualquier contenedor existente con el mismo nombre.
       5. Ejecución del Comando: Utiliza un `ProcessBuilder` para ejecutar el comando en un contenedor Docker.
       6. Gestión de Recursos: Utiliza `AutoCloseableExecutor` y `AutoCloseableProcess` para asegurar que los recursos se cierren adecuadamente.
       7. Manejo de Excepciones: Maneja excepciones como `TimeoutException`, `ExecutionException` y `InterruptedException` para asegurar que el contenedor se elimine en caso de error.

##### createProcessBuilder

Este método crea un `ProcessBuilder` configurado con el comando y el shell adecuado para el sistema operativo.

##### cleanUpContainers

Este método se encarga de eliminar cualquier contenedor existente con el nombre especificado. Realiza los siguientes pasos:

       1. Listar Contenedores: Utiliza un `ProcessBuilder` para listar los contenedores existentes.
       2. Eliminar Contenedores: Elimina cada contenedor encontrado utilizando otro `ProcessBuilder`.

##### executeCommand

Este método ejecuta un comando en el contenedor Docker y recoge la salida. Realiza los siguientes pasos:

       1. Ejecución del Proceso: Inicia el proceso utilizando el `ProcessBuilder`.
       2. Lectura de la Salida: Lee la salida del proceso utilizando un `BufferedReader`.
       3. Manejo del Código de Salida: Verifica el código de salida del proceso para determinar si la ejecución fue exitosa.
       4. Manejo de Excepciones: Maneja excepciones como `IOException` e `InterruptedException` para asegurar que el hilo actual se interrumpa adecuadamente.


## CodeProcessingManager

La clase `CodeProcessingManager` es responsable de gestionar el flujo de procesamiento del código del usuario. Utiliza el `DockerExecutor` para ejecutar el código en un contenedor Docker y aplica una cadena de filtros para validar y procesar el código antes de su ejecución.

#### Métodos

  ##### `processCode`

Este método es el punto de entrada principal para procesar el código del usuario. Realiza los siguientes pasos:

1. **Aplicación de Filtros**: Aplica una cadena de filtros al código del usuario utilizando `filterChain`.
2. **Verificación de Éxito**: Verifica si la aplicación de filtros fue exitosa.
3. **Ejecución del Código**: Si la aplicación de filtros fue exitosa, utiliza `DockerExecutor` para ejecutar el código en un contenedor Docker.
4. **Manejo de Excepciones**: Maneja excepciones como `IOException` e `InterruptedException` para asegurar que el contenedor se elimine en caso de error.
5. **Calculación de la puntuación**: Calcula la puntuación del código del usuario basada en el resultado de la ejecución.

##### `calculateScore`

Este método calcula la puntuación del código del usuario basado en el resultado de la ejecución. Realiza los siguientes pasos:

1. **Verificación de Compilación**: Verifica si el código se compiló correctamente.
2. **Verificación de Ejecución**: Verifica si el código se ejecutó correctamente.
3. **Comparación de Resultados**: Compara el resultado de la ejecución con el resultado esperado.
4. **Asignación de Puntuación**: Asigna una puntuación basada en el resultado de la comparación.

