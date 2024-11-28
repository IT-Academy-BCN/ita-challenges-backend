package com.itachallenge.score;


import com.itachallenge.score.service.JavaFileService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.io.File;
import java.io.IOException;


@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(info = @Info(title = "Ita Backend Score", version = "1.0", description = "Description"))
public class App {

    public static void main(String[] args) {
        JavaFileService javaFileService = new JavaFileService();
        String storagePath = "C:\\temp\\java-files";
        javaFileService.setStoragePath(storagePath);

        // Ensure the directory exists
        File directory = new File(storagePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String userCode = "System.out.println(\"Hello, World!\");";
        String fileName = "UserSolution.java";

        try {
            File javaFile = javaFileService.createJavaFile(userCode, fileName);
            System.out.println("File created at: " + javaFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   // public static void main(String[] args) {
     //   SpringApplication.run(App.class, args);
    }






