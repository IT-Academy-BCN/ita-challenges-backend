# Cómo Crear un JRE Personalizado desde Windows

Este documento describe los pasos necesarios desde Windows para crear un JRE personalizado utilizando `jlink`.
Todo el proceso se realiza dentro de un contenedor Docker, lo que nos permite generar un JRE optimizado y compatible con el sistema operativo **Alpine Linux**, que es el que utilizamos en el **Sandbox** donde se ejecutará y probará el código de los usuarios.

## **1. Levantar un contenedor Docker con OpenJDK**

El primer paso es crear un nuevo contenedor utilizando la imagen oficial de OpenJDK para configurar un entorno en el que personalizar el JRE.
Para ello, abrimos la terminal y ejecutamos el siguiente comando:

```bash
docker run -it --rm openjdk:21-jdk /bin/bash
```

Esto iniciará un contenedor basado en OpenJDK 21 con un shell interactivo.
Si todo ha ido correctamente, estaremos ya dentro del contenedor. Al usar el flag ```--rm```, este contenedor se eliminará automáticamente al salir.

## **2. Crear el JRE personalizado con jlink**

Dentro del contenedor, generaremos el JRE personalizado siguiendo estos pasos:

#### 2.1. Identificar los módulos necesarios

Debemos seleccionar los módulos requeridos por nuestro JRE personalizado.
Estos son los módulos seleccionados hasta ahora, aunque podríamos necesitar añadir o excluir alguno más en el futuro:

- java.base
- java.logging
- java.prefs
- java.rmi
- java.sql
- jdk.charsets
- java.desktop

#### 2.2. Ejecutar el comando jlink

Usamos jlink para generar el JRE personalizado utilizando este comando dentro del contenedor:

```bash
jlink --module-path $JAVA_HOME/jmods \
--add-modules java.base,java.logging,java.prefs,java.rmi,java.sql,jdk.charsets,java.desktop \
--strip-debug --compress=2 --no-header-files --no-man-pages \
--output /custom-jre
```

Esto creará un JRE personalizado en el directorio /custom-jre dentro del contenedor. Podemos modificar el comando para añadir o excluir otros módulos, así como cambiar el directorio de salida.

## **3. Copiar el JRE generado al sistema anfitrión**

Para copiar el JRE personalizado desde dentro del contenedor a nuestro sistema, debemos seguir estos pasos:

#### 3.1. Comprimir el JRE dentro del contenedor

Ejecutamos el siguiente comando para crear un archivo comprimido del JRE:

```bash
tar -czvf /custom-jre.tar.gz /custom-jre
```

#### 3.2. Identificar el ID del contenedor

En otra terminal a parte (es decir, fuera del contenedor), verificamos el ID de nuestro contenedor en ejecución con el siguiente comando:

```bash
docker ps
```
Nos aparecerá un listado detallado de nuestros contenedores en ejecución, donde deberemos fijarnos en el ID del contenedor en cuestión.

#### 3.3. Copiar en nuestra máquina el archivo comprimido desde el contenedor

Usaremos ```docker cp``` para copiar el archivo comprimido del contenedor en nuestro ordenador. 
Todavía desde la terminal nueva, ejecuta este comando sustituyendo ```<container_id>``` por el ID real que copiamos en el paso anterior y ```C:\Users\Pepito\Desktop\custom-jre``` por la ruta donde queremos que se copie el archivo:

```bash
docker cp <container_id>:/custom-jre.tar.gz C:\Users\Pepito\Desktop\custom-jre
```

Ejemplo:

```bash
docker cp 800a361bc05c:/custom-jre.tar.gz C:\Users\Pepito\Desktop\custom-jre
```

#### 3.4. Descomprimir el archivo ```custom-jre.tar.gz``` en nuestra máquina

Ahora debemos descomprimir el archivo en nuestro ordenador.

Existen dos métodos:

1. Utilizando la terminal:

Ejecutaremos este comando desde la nueva terminal, modificando las rutas según corresponda:

```bash
tar -xvzf "C:\Users\Pepito\Desktop\custom-jre\custom-jre.tar.gz" -C "C:\Users\Pepito\Desktop\custom-jre"
```

2. Utilizando 7-Zip:

- Haz clic derecho sobre el archivo comprimido.
- Selecciona "7-Zip > Extraer aquí". Se descomprirá el archivo ```custom-jre.tar.gz``` en otro archivo ```custom-jre.tar```.
- Repite el proceso sobre el nuevo archivo ```custom-jre.tar``` para que finalmente lleguemos a tener la carpeta descomprimida ```custom-jre```.

#### 3.5.  Verificar los módulos del JRE personalizado

Accede a la subcarpeta ```legal``` dentro del JRE descomprimido y verifica que se incluyen los módulos necesarios.

## **4. Finalizar y limpiar**

Si todo está correcto y no necesitas rehacer el JRE, vuelve a la terminal anterior (dentro del contenedor) y ejecuta el comando ```exit```.
Al salir del contenedor con este comando, se eliminará automáticamente gracias al flag ```--rm``` que utilizamos en la creación del contenedor.

## **Notas Adicionales**

- Asegúrate de tener los permisos adecuados en tu sistema para ejecutar los comandos de Docker y para descomprimir los archivos.

- Este proceso puede repetirse tantas veces como haga falta en caso de que sea necesario crear un nuevo JRE o el resultado no haya sido el esperado.

- Si insertas el JRE personalizado en el proyecto, recuerda revisar todos los archivos del directorio del JRE, pues es posible que alguno de ellos se haya añadido automáticamente al ```.gitignore```. Si fuera el caso, desde el IDE, haz click derecho sobre el archivo en cuestión, selecciona "Git > Add" para que se añada al repositorio.