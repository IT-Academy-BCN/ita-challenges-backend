# Cómo Crear un JRE Personalizado desde Windows

Este documento describe los pasos necesarios desde Windows para crear un JRE personalizado utilizando `jlink`.
Todo el proceso se realiza dentro de un contenedor Docker, lo que te permitirá generar un JRE optimizado y compatible con el sistema operativo **Alpine Linux**, que es el que se utiliza en el **Sandbox** donde se ejecutará y probará el código del usuario.

## **1. Levanta un contenedor Docker con OpenJDK**

El primer paso es crear un nuevo contenedor utilizando la imagen oficial de OpenJDK para configurar un entorno en el que puedas personalizar el JRE.
Para ello, abre la terminal y ejecuta el siguiente comando:

```bash
docker run -it --rm openjdk:21-jdk /bin/bash
```

Esto iniciará un contenedor basado en OpenJDK 21 con un shell interactivo.
Si todo ha ido correctamente, estarás ya dentro del contenedor. Al usar el flag ```--rm```, este contenedor se eliminará automáticamente cuando salgas de él.

## **2. Crea el JRE personalizado con jlink**

Dentro del contenedor, genera el JRE personalizado siguiendo estos pasos:

#### 2.1. Identifica los módulos necesarios

Selecciona los módulos que quieres incluir en el JRE personalizado.
Por ejemplo, estos son los módulos que se seleccionaron para crear el primer JRE personalizado del proyecto, aunque puede que necesites añadir o excluir alguno más:

- java.base
- java.logging
- java.prefs
- java.rmi
- java.sql
- jdk.charsets
- java.desktop

#### 2.2. Ejecuta el comando jlink

Usa ```jlink``` para generar el JRE personalizado ejecutando este comando dentro del contenedor:

```bash
jlink --module-path $JAVA_HOME/jmods \
--add-modules java.base,java.logging,java.prefs,java.rmi,java.sql,jdk.charsets,java.desktop \
--strip-debug --compress=2 --no-header-files --no-man-pages \
--output /custom-jre
```

Esto creará un JRE personalizado con los módulos especificados en el directorio ```/custom-jre``` (dentro del contenedor). Puedes modificar el comando para añadir o excluir otros módulos, así como cambiar la ruta del directorio de salida si fuese necesario.

## **3. Copia en tu máquina el JRE generado**

Asegúrate de obtener una copia del JRE personalizado antes de destruir el contenedor, ya que el JRE se desturirá con él.
Para copiar en tu sistema el JRE personalizado, debes seguir estos pasos:

#### 3.1. Comprime el JRE dentro del contenedor

Ejecuta el siguiente comando para crear un archivo comprimido del JRE y así facilitar su transferencia:

```bash
tar -czvf /custom-jre.tar.gz /custom-jre
```

#### 3.2. Identifica el ID del contenedor

Abre una **nueva terminal** para trabajar fuera del contenedor (sin cerrar la anterior) y verifica el ID de tu contenedor en ejecución con el siguiente comando:

```bash
docker ps
```
Este comando muestra un listado detallado de los contenedores en ejecución. Asegúrate de copiar el ID del contenedor donde has creado el JRE personalizado.

#### 3.3. Copia en tu máquina el archivo comprimido del contenedor

**Desde la nueva terminal**, ejecuta el siguiente comando para copiar el archivo comprimido del contenedor en tu ordenador (sustituye ```<container_id>``` por el ID real que copiaste en el paso anterior y ```<path>``` por la ruta en la que quieras que se copie el archivo):

```bash
docker cp <container_id>:/custom-jre.tar.gz <path>
```

Ejemplo:

```bash
docker cp 800a361bc05c:/custom-jre.tar.gz C:\Users\Michel\Desktop\custom-jre
```

#### 3.4. Descomprime el archivo ```custom-jre.tar.gz``` en tu máquina

Utiliza uno de estos dos métodos para descomprimir el archivo en tu ordenador:

1. A través de la terminal:

**Desde la nueva terminal**, ejecuta el siguiente comando (modificando las rutas según corresponda):

```bash
tar -xvzf "compressed_file_path" -C "output_path"
```

Ejemplo:

```bash
tar -xvzf "C:\Users\Michel\Desktop\custom-jre\custom-jre.tar.gz" -C "C:\Users\Michel\Desktop\custom-jre"
```

2. Utilizando 7-Zip:

- Haz clic derecho sobre el archivo comprimido ```custom-jre.tar.gz```.
- Selecciona "7-Zip > Extraer aquí". Se descomprimirá en el archivo ```custom-jre.tar```.
- Repite el proceso sobre el archivo ```custom-jre.tar``` para obtener finalmente la carpeta descomprimida ```custom-jre```.

#### 3.5.  Verifica los módulos de tu JRE personalizado

Accede a la subcarpeta ```custom-jre\legal``` y verifica que se hayan incluido los módulos necesarios.

## **4. Finalizar y limpiar**

Si está todo correcto y no necesitas rehacer el JRE, regresa a la terminal anterior (es decir, dentro del contenedor) y ejecuta el comando ```exit```.
Al salir del contenedor, este se eliminará automáticamente gracias al flag ```--rm``` que utilizamos en la creación del contenedor.

## **Notas Adicionales**

- Asegúrate de tener los permisos adecuados en tu sistema para poder ejecutar los comandos de Docker y descomprimir los archivos correctamente.

- Este proceso puede repetirse tantas veces como haga falta en caso de que sea necesario crear un nuevo JRE o el resultado no haya sido el esperado.

- Si insertas el JRE personalizado en el proyecto, recuerda revisar todos los archivos del directorio y subdirectorios del JRE, pues es posible que alguno de ellos se haya añadido automáticamente al ```.gitignore```. Si fuera el caso, desde el IDE, haz clic derecho sobre el archivo en cuestión y selecciona "Git > Add" para que se añada al repositorio.