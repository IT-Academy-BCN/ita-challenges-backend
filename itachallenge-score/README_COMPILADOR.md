### Notas para el compilador

En el proyecto actual se ha utilizado la librería **Janino** para compilar y ejecutar
código de manera dinámica. El método compileAndRunCode en la clase CodeExecutionService
es el encargado de compilar y ejecutar el código. Hay que modificar el proceso porque
esto genera problemas graves de seguridad al permitir la entrada de código por parte del
usuario, esto permite que el usuario ejecute código malicioso en el servidor.


##### Proceso para compilar con seguridad

Es necesario verificar la seguridad para evitar la inyección de código malicioso
por parte del usuario
1. Sandbox - crear un contenedor docker de manera dinámica
2. Verificación del código - investigar librería Janino
3. Limitación de permisos - sistema de seguridad de Java para limitar los permisos del código compilado.
4. Personalización ClassLoader - para controlar qué clases puede cargar el código compilado.



### Librerías útiles

Librería Janino para compilation y ejecución dinámica
Útil para la compilación, ejecución y análisis del código introducido por el usuario. Al usar esta librería no tenemos que crear un archivo temporal, al compilar el código janino se encarga de guardar el archivo a compilar en memoria y ejecutarlo.


- *All these considerations lead to compilation of Java code at run-time, like some engines (e.g. JSP engines)
  already do. However, compiling Java programs with ORACLE's JDK is a relatively resource-intensive process
  (disk access, CPU time, ...). This is where Janino comes into play... a light-weight, "embedded" Java compiler
  that compiles simple programs in memory into JVM bytecode which executes within the JVM of the running program.*
    - https://janino-compiler.github.io/janino/
    - https://janino-compiler.github.io/janino/apidocs/


- **Janino as a Code Analyser**
  *Apart from compiling Java code, JANINO can be used for static code analysis: Based on the AST ("abstract syntax tree")
  produced by the parser, the [Traverser](http://janino.unkrig.de/javadoc/org/codehaus/janino/util/Traverser.html) walks
  through all nodes of the AST, and derived classes can do all kinds of analyses on them.*


- **Janino as a Script Evaluator**
  *Analogously to the expression evaluator, a ScriptEvaluator API exists that compiles and processes a Java "block", i.e. the body of a method.
  If a return value other than "void" is defined, then the block must return a value of that type.*


- **Librería Docker-Java para gestión docker dinámico**
  Útil para la creación de Sanbox y correr en un entorno seguro.
    - https://github.com/docker-java/docker-java/blob/main/docs/getting_started.md
    - https://docs.docker.com/engine/api/sdk/
    - implementation 'com.github.docker-java:docker-java:3.2.11' - para el build.gradle


**Explicación de la ejecución del código - CodeExecutionService línea 33:**
- `ByteArrayOutputStream outputStream = new ByteArrayOutputStream();` Esta línea crea un nuevo objeto `ByteArrayOutputStream`. Este objeto se utiliza para capturar la salida del código que se va a ejecutar.
- `PrintStream printStream = new PrintStream(outputStream);` Aquí se crea un nuevo objeto `PrintStream` que utiliza el `ByteArrayOutputStream` creado en el paso anterior. `PrintStream` es una clase que proporciona métodos para escribir datos de salida de varias maneras.
- `PrintStream old = System.out;` Esta línea guarda la salida estándar actual (normalmente la consola) en la variable `old`. Esto se hace para que pueda ser restaurada más tarde.
- `System.setOut(printStream);` Aquí se redirige la salida estándar del sistema al `PrintStream` creado en el paso 2. Esto significa que cualquier cosa que se escriba en la salida estándar a partir de este punto será capturada por el `ByteArrayOutputStream`.
- `Class<?> compiledClass = compiler.getClassLoader().loadClass("Main");` Esta línea carga la clase `Main` del cargador de clases del compilador. Esta es la clase que contiene el código que se va a ejecutar.
- `compiledClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});` Aquí se obtiene el método `main` de la clase `Main` y se invoca sin argumentos. Esto ejecuta el código fuente que se ha compilado.
- `System.out.flush();` Esta línea vacía cualquier dato restante en el `PrintStream`, asegurando que todo se haya escrito en el `ByteArrayOutputStream`.
- `System.setOut(old);` Finalmente, esta línea restaura la salida estándar original del sistema. Esto significa que cualquier cosa que se escriba en la salida estándar después de este punto irá a la consola (o donde sea que estuviera dirigida la salida estándar originalmente).
