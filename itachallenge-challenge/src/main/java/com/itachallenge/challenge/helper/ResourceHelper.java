package com.itachallenge.challenge.helper;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResourceHelper {

    private Resource resource;
    private String resourcePath;

    public ResourceHelper(@NotNull String resourcePath) {
        this.resourcePath = resourcePath;
        resource = new ClassPathResource(resourcePath);
    }

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
