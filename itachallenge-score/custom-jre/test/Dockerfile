# EJEMPLO DOCKERFILE PARA PROBAR EL JRE PERSONALIZADO

# Usa Alpine como base (con la versión correspondiente)
FROM alpine:latest

# Copia el JRE personalizado al contenedor
COPY custom-jre /custom-jre

# Establece la variable de entorno JAVA_HOME al JRE personalizado
ENV JAVA_HOME=/custom-jre
ENV PATH=$JAVA_HOME/bin:$PATH

# Establece el directorio de trabajo
WORKDIR /app

# Copia la clase compilada HelloWorld.class dentro del contenedor (recuerda editar el nombre de la clase aque corresponda)
COPY HelloWorld.class /app/

# Comando por defecto para ejecutar el programa (recuerda editar el nombre de la clase aque corresponda)
CMD ["java", "HelloWorld"]