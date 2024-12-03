package com.itachallenge.score.service;

import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Setter
@Service
public class JavaFileService {

    private static final Logger log = LoggerFactory.getLogger(JavaFileService.class);

    // Private constructor to hide the implicit public one
    private JavaFileService() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static File createJavaFile(String userCode, String fileName, String storagePath) throws IOException {
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
        FileUtils.writeStringToFile(javaFile, finalCode, StandardCharsets.UTF_8);

        log.info("Java file created successfully at: {}", javaFile.getAbsolutePath());
        return javaFile;
    }
}