package com.itachallenge.challenge.helper;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ResourceHelper {

    private Resource resource;
    private String resourcePath;

    public ResourceHelper() {
        //TODO: refactor constructor and method
        /*
        No implementado para no 'molestar ni crear conflicto' a alfonso en la implementación feature#5
        Simplemente es:
            Now:
            new ResourceHelper().readResourceAsString(path);
            Once refactored:
            new ResourceHelper(path).readResourceAsString();
         Clase mucho mejor diseñada y cohesiva si tiene como atributo el Resource "que va a manipular".
         Futuros métodos más simples (ej: mapResourceToTargetClass(Class<T> targetClass) ).
            No es YAGNI, si los tests lo usan se reduciría bastante su código y se eviatría copy paste.
         */
    }

    //https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
    public String readResourceAsString (String resourcePath)  throws IOException{
        this.resourcePath = resourcePath;
        resource = new ClassPathResource(resourcePath);
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
