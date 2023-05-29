package com.itachallenge.challenge.helper;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/*
Clase puede ser de mucha utilidad para mejorar la eficiencia
en crear tests (ej: load json for expected data)
 */
public class ResourceHelper {

    private Resource resource;
    private String resourcePath;

    //if path null -> ClassPathResource throws IllegalArgumentException
    public ResourceHelper(@NotNull String resourcePath) {
        this.resourcePath = resourcePath;
        resource = new ClassPathResource(resourcePath);
    }

    //https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
    public String readResourceAsString ()  throws IOException{
        try {
            File file = resource.getFile();
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            String msg =getResourceErrorMessage("loading/reading").concat(ex.getMessage());
            throw new IOException(msg);
        }
    }

    private String getResourceErrorMessage(String action){
        String resourceIdentifier = Objects.requireNonNullElseGet(resourcePath, () -> resource.getDescription());
        return "Exception when " + action + " " + resourceIdentifier + " resource: \n";
    }
}