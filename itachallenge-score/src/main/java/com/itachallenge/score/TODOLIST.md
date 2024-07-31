### Tareas para feature#568

1. **Segmentar tarjeta feature#568 en varias**

2. **Crear clase para revertir caracteres de Java desde el frontend**

```java
public class EscapeJava {
    public static String escapeJavaCode(String value) {
        return value
            .replace("\\", "\\\\") // Replace backslashes
            .replace("\"", "\\\"") // Replace double quotes
            .replace("'", "\\'") // Replace single quotes
            .replace("\n", "\\n") // Replace new line
            .replace("\r", "\\r") // Replace carriage return
            .replace("\t", "\\t") // Replace tab
            .replace("\f", "\\f") // Replace form feed
            .replace("<", "\\u003C") // Replace less than
            .replace(">", "\\u003E") // Replace greater than
            .replace("&", "\\u0026"); // Replace ampersand
    }
}
```
3. Crear clase para asignar caracteres ASCII válidos y linkearlos a collections

     Evaluar el rendimiento de hashmaps, linkedtrees, arrays, maps, etc.
     Evitar el uso de la palabra synchronize.
       Más información:

            Piensa en Java - Bruce Eckel
            Thinking in Java - 4th Edition
            
            
4. Crear un contenedor de Java limitado

        Utilizar LoadClass para evitar el uso de librerías que puedan inyectar código malicioso.
        
5. Aplicar filtros en el controller
        
        Filtrar caracteres ASCII y los caracteres recibidos desde el frontend.
        Pasar el código (string) al contenedor de Java limitado.
   
6. Utilizar el compilador de Janino en el contenedor

    Ejecutar el código dentro del contenedor.
    Devolver los resultados de compilación, ejecución y el resultado.
    java
