package com.itachallenge.challenge.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourcesUtils {

    private Resource resource;

    private String resourcePath;

    private ObjectMapper mapper;

    public ResourcesUtils(String resourcePath) {
        resource = new ClassPathResource(resourcePath);
        this.resourcePath = resourcePath;
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    //Constructor to use if Resource is injected in client.
    // Ex:
    //@Value("classpath:json/xxxx.yyy")
    //private Resource targetResource;
    public ResourcesUtils(Resource resource) {
        this.resource = resource;
        try {
            //if the resource can't be resolved as url
            resourcePath = resource.getURL().getPath();
        } catch (IOException e) {
            resourcePath = null;
        }
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String getResourceErrorMessage(String action){
        if(resourcePath != null){
            return  "Exception occurred when "+action+" "+resourcePath+" resource: \n";
        }else {
            return  "Exception occurred when "+action+" "+resource.getDescription()+" resource: \n";
        }
    }

    //TEST OK:
    //https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FileUtils.html
    public String readResourceAsString () throws IOException {
        try {
            // .getFile can throw IOException if file can't be read (not found or invalid resourcePath)
            File file = resource.getFile();
            // .readFileToString can throw:
            // FilteNotFoundException (IOException)  if not readable file
            // NullPointException (RuntimeException) if file is null -> won't happen (prvious sentence will throw IOException)
            // IOException if I/O problems
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            String msg =getResourceErrorMessage("loading/reading").concat(ex.getMessage());
            //Good practice: no lanzar un RuntimeException (o derivadas)
            //Implica propagar la excepción y que el cliente la gestione
            throw new IOException(msg);
        }
    }

    //TEST OK:
    public String readResourceAsStringV2()throws IOException{
        try {
            //FileNotFounException if underlying resource not exists
            //IOException if the content can be opened as stream
            InputStream inputStream =resource.getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            //IOException in case I/O errors
            String resourceString = FileCopyUtils.copyToString(reader);
            //.copyToString closes the reader
            //IOException in case I/O errors
            inputStream.close();
            return resourceString;
        } catch (IOException ex) {
            //lo que se hacía en el blog que vi:
            //throw new UncheckedIOException(e); //Runtime exception, witch wraps an IOException

            String msg =getResourceErrorMessage("loading/reading").concat(ex.getMessage());
            //Good practice: no lanzar un RuntimeException (o derivadas)
            //Implica propagar la excepción y que el cliente la gestione
            throw new IOException(msg);
        }
    }

    //--------------------------------------------
    //Mapping methods:

    private File getFile()throws IOException{
        try {
            return resource.getFile();
        } catch (IOException ex) {
            String msg =getResourceErrorMessage("loading/reading").concat(ex.getMessage());
            throw new IOException(msg);
        }
    }

    //TODO: test
    /**
     * Works if target class is X.class (or X[].class)
     * Ex:
     * List<X>.class -> no compile
     * List.class compile, but returns List<Object> (so client needs to downcast).
     *-> Instead of downcast -> use next methods:
     *
     * @param targetClass:
     * @return
     * @param <T>
     * @throws IOException
     */
    public <T> T mapResourceToClass(Class<T> targetClass) throws IOException{
        File resourceAsFile = getFile();

        try {
            //working option
            //return T resultObject = mapper.readValue(readResourceAsString(), targetClass);

            //throws many, all excends JsonProcessionException (inheriths from IOException)
            return mapper.readValue(resourceAsFile, targetClass);
        } catch (IOException ex) {
            String msg = getResourceErrorMessage("mapping to "+targetClass)
                    .concat(ex.getMessage());
            throw new IOException(msg);
        }
    }

    //TODO: verify + test
    /**
     * !!!!!!!!!!!  IMPORTANT: NOT SURE IF WORKS  !!!!!!!!!!!!!!!!
     * But I'm sure this sentence works:
     *      mapper.readValue(source, new TypeReference<List<MyClass>() {});
     *
     * @param targetWrappedClass
     * @return
     * @param <T>
     * @throws IOException
     */
    public <T> List<T> mapResourceToListOf(Class<T> targetWrappedClass)
            throws IOException{
        File resourceAsFile = getFile();
        TypeReference<List<T>> typeRef = new TypeReference<>() {};
        try {
            return mapper.readValue(resourceAsFile, typeRef);
        } catch (IOException ex) {
            String msg = getResourceErrorMessage("mapping to list of "+targetWrappedClass)
                    .concat(ex.getMessage());
            throw new IOException(msg);
        }
    }


}
