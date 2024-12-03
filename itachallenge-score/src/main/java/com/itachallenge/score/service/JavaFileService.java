package com.itachallenge.score.service;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Setter
@Service
public class JavaFileService {

    private static final Logger log = LoggerFactory.getLogger(JavaFileService.class);

    @Value("${java.file.storage.path:/tmp}") // Path temporal por defecto: /tmp
    private String storagePath;

    public File createJavaFile(String userCode, String fileName) throws IOException {

        String filePath = storagePath + File.separator + fileName;
        log.info("Creating Java file at: {}", filePath);

        String boilerplate = """
                    import java.util.List;
                    import java.util.ArrayList;
                
                    public class UserSolution {
                        public static void main(String[] args) {
                            try {
                                // User code:
                
                                %s
                
                            } catch (Exception e) {
                                System.err.println("Error executing user code:");
                                e.printStackTrace();
                            }
                        }
                    }
                """;

        String finalCode = String.format(boilerplate, userCode);

        File javaFile = new File(filePath);
        try (FileWriter writer = new FileWriter(javaFile)) {
            writer.write(finalCode);
        }

        log.info("Java file created successfully at: {}", javaFile.getAbsolutePath());
        return javaFile;
    }

}