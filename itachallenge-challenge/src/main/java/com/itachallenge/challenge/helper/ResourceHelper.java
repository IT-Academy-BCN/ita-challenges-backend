package com.itachallenge.challenge.helper;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/*
Clase puede ser de mucha utilidad para mejorar la eficiencia
en crear tests (ej: load json for expected data)
 */
public class ResourceHelper {

    private Resource resource;
    private String resourcePath;
    private static final Logger log = LoggerFactory.getLogger(ResourceHelper.class);

    //if path null -> ClassPathResource throws IllegalArgumentException
    public ResourceHelper(@NotNull String resourcePath) {
        this.resourcePath = resourcePath;
        resource = new ClassPathResource(this.resourcePath);
    }

    //https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
    public Optional<String> readResourceAsString (){

        Optional<String> result = Optional.empty();
        try {
            result = Optional.of(FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            log.error(getResourceErrorMessage("loading/reading").concat(ex.getMessage()));
        }
        return result;
    }

    private String getResourceErrorMessage(String action){
        String resourceIdentifier = Objects.requireNonNullElseGet(resourcePath, () -> resource.getDescription());
        return "Exception when " + action + " " + resourceIdentifier + " resource: \n";
    }
}