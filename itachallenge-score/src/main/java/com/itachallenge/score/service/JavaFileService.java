package com.itachallenge.score.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class JavaFileService {

    private static final Logger log = LoggerFactory.getLogger(JavaFileService.class);

    // Inyectamos la ruta desde el archivo de configuración
    @Value("${java.file.storage.path}")
    private String storagePath;

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    // Método para crear el archivo Java con el código del usuario
    public File createJavaFile(String userCode, String fileName) throws IOException {
        // Usamos File.separator para construir la ruta de manera segura
        String filePath = storagePath + File.separator + fileName; // Usamos File.separator en lugar de "/"

        log.info("Creando archivo Java en: {}", filePath);

        // Plantilla Boilerplate de Java con manejo de errores
        String boilerplate = """
                public class UserSolution {
                    public static void main(String[] args) {
                        try {
                            // User code:
                            %s
                        } catch (Exception e) {
                            System.err.println("Error en la ejecución del código del usuario:");
                            e.printStackTrace();
                        }
                    }
                }
                """;

        String finalCode = String.format(boilerplate, userCode);

        // Crear el archivo en la ruta proporcionada
        File javaFile = new File(filePath); // El archivo que se va a generar
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(finalCode); // Escribir el código final en el archivo
        }

        log.info("Archivo Java creado exitosamente en: {}", javaFile.getAbsolutePath());
        return javaFile; // Retorna el archivo creado
    }
}