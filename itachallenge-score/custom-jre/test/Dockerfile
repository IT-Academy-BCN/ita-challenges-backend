# Usa Alpine como base
FROM alpine:latest

# Copia el JRE personalizado al contenedor
COPY custom-jre /custom-jre

# Establece la variable de entorno JAVA_HOME al JRE personalizado
ENV JAVA_HOME=/custom-jre
ENV PATH=$JAVA_HOME/bin:$PATH

# Establece el directorio de trabajo
WORKDIR /app

# Copia la clase compilada HelloWorld.class dentro del contenedor
COPY HelloWorld.class /app/

# Comando por defecto para ejecutar el programa
CMD ["java", "HelloWorld"]