package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.exceptions.ResourceException;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResourceHelper {

    public byte[] readResourceAsByteArray (String resourcePath) {
        try {
            File file = new ClassPathResource(resourcePath).getFile();
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new ResourceException(e.getMessage());
        }
    }


    public String readResourceAsString (String resourcePath) {
        try {
            File file = new ClassPathResource(resourcePath).getFile();
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ResourceException(e.getMessage());
        }
    }

    public String readResourceAsStringV2(String resourcePath){
        try {
            InputStream inputStream = new ClassPathResource(resourcePath).getInputStream();
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            String resourceString = FileCopyUtils.copyToString(reader);
            //.copyToString closes the reader
            inputStream.close();
            return resourceString;
        } catch (IOException e) {
            throw new ResourceException(e.getMessage());
        }
    }
}
