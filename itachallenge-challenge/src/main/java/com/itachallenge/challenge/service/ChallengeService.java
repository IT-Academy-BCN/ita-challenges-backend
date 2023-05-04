package com.itachallenge.challenge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helper.ResourceHelper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


@Service
public class ChallengeService {

    private String JsonResourcePath = "/data-challenge.json";

    public Mono<String> getAllChallenges() throws IOException {

        ResourceHelper helper = new ResourceHelper();

        String JsonResource = helper.readResourceAsString(JsonResourcePath);

        return Mono.just(JsonResource);
        /*
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data-challenge.json");
            ObjectMapper objectMapper = new ObjectMapper();
            String challenges = String.valueOf(objectMapper.readTree(inputStream));

            return Mono.just(challenges);
        } catch (IOException e) {
            return Mono.error(e);
        }*/
    }

}
