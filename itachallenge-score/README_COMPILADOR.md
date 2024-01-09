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
