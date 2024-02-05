package com.itachallenge.document.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.document.proxy.IChallengeClient;
import com.itachallenge.document.proxy.IUserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final IChallengeClient challengeClient;
    private final IUserClient userClient;

    private final ObjectMapper objectMapper;

    public DocumentService(IChallengeClient challengeClient, IUserClient userClient1,
                           ObjectMapper objectMapper) {
        this.challengeClient = challengeClient;
        this.userClient = userClient1;
        this.objectMapper = objectMapper;
    }

    public String getSwaggerUserDocsStr() {
        return userClient.getSwaggerDocs();
    }
    public String getSwaggerChallengeDocsStr() {
        return challengeClient.getSwaggerDocs();
    }


    public JsonNode getSwaggerDocs() {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(challengeClient.getSwaggerDocs());
        } catch (JsonProcessingException e) {
            log.error(e.toString());
        }
        return jsonNode;
    }

}
