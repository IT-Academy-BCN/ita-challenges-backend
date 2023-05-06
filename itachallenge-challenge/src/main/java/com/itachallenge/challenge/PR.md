# TODO:
- Veure com solucionar lo del crlf (retorns de linia extranys en ResourceHelperTest)
- Un cop tingui el test ok guardar ResourcesUtils + test equivalent en la meva carpeta utils

### 1. Campo count en filtros eliminado
- "No termino de pillar" porque hay que pasar un campo count en los dtos. El FE no puede hacer algo tipo 'getNameArrayField.length/size'?. O es por ponerles las cosas fáciles y/o porque a veces no convierten el json a objeto sino que trabaja con el a full string?
- Debido a esto, puse count como pareja de cualquier array en el json (ya que calqué lo que se hacía con el GenericResult de BussinessAssistant).
- Ahora:
  - **en filtros ningún campo count**.
  - **TODO: en sort info aun está (no me dijiste nada de el). Es porque se te pasó pero hay que quitarlo porque el FE no lo necesita? O en este caso si que hay que dejarlo?**

### 1. Dummies properties externalizadas
- Paths en application.yml + DummiesConfig

### 1. Only 1 suite test left
Soy consciente que eran redundantes, pero lo hice así por:
   - Mayor flexibilidad para lanzar el conjunto de tests desados.
   - Para que un suite test pueda englobar a N test classes, estas deben ser "visibles" para el suite test -> AllTests solo "ve" estos test class concretos si estas tienen una visibilidad "public". 
      - opción a) Previo en PR. Un suite test en cada paquete (públic para que AllTest "lo vea"), i que esté "vea" los tests de su paqueta (que tenían visibilidad default).
      - opción b) Actual. Solo un suite test (AllTest), pero obliga a que todos las classes tests tengan visibilidad pública. ->Sonar se queja: Smell code info java:S5786 -> JUnit5 test classes can have any visibility but private, **however, it is recommended to use the default package visibility**, which improves readability of code. -> Es solo "smell code info", pero preferí seguir la recomendación de sonar
### 2. Removed ResourceException(extends Runtime)
- La implementé así para que los clientes no tubieran que gestionarla (ya que la idea original era que ResourceHelper sirviera de apoyo a los tests para generar "expected data").
- Ahora ResourceHelper propaga una IOException con custom mensage.
- Service gestiona excepción -> en caso de producir-se devuelve Mono< String > con el mensaje.
- Wrapped type String, para que sea coherente con el type devuelto cuendo la funcionalidad usa dummies.
- **TODO:** response status no debería ser OK -> Internal error? controller advice?
### 2.2 Gestión de errores y excepciones
No termino de "pillar" lo de la gestión de errores. Por lo que vi, basándome en BussinessAssistant:
1. Servicio crea un ErrorDto con el mensaje (y lo mete dentro del dto responsable de la respuesta si procede).
2. Controlador devuelve siempre al front un Mono que publica "algo".
3. Entonces: 
   - El mono puede publicar tanto lo que el front pedía como "algo" con el mensaje de error.
   - cuando el front se subscribe al observable, la primera lógica que debe ejecutrase en "on next" es: **must comprovar if (lo "observado" contiene "lo esperado") {do success logic} else ("algo con un mensaje de error") {do fail logic}?**
   - El front no implementa lógica en "on error" cuando se subscribe?
4. **Probablemente se me esté escapando muchas cosas (debido a que ni idea de como funciona el front)** pero no seria más fácil hacer que:
   1. El publicador que devuelve el BE siempre publica lo que el FE espera (success).
   2. En caso de error/excepcion el BE propaga un Mono.error (que tiene la excepción con el mensaje) (fail / problem)
   3. El FE, cuando se subcribe: 
      1. "on next" -> todo ha ido bién -> logica de success
      2. "on error" -> ha habido fallo -> lógica de fail (pudiendo hacer error.getMessage)
5. No seria más simple, tanto para back como para front?

### 3 Diseño de ResourceHelper + mockeo en en service test
1. ResourceHelper mocked in ChallengeServiceTest
2. Clase ResourceHelper NO estereotipada (no bean, ya que vale cualquier objeto).
3. Dependencia del service al helper es de uso (como variables locales).
4. **Si prefieres que sea bean + inyectado como atributo en service lo canvio**.