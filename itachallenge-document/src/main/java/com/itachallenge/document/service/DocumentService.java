package com.itachallenge.document.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.document.controller.DocumentController;
import com.itachallenge.document.proxy.IChallengeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private IChallengeClient challengeClient;

    private final ObjectMapper objectMapper;

    public DocumentService(IChallengeClient challengeClient,
                           ObjectMapper objectMapper) {
        this.challengeClient = challengeClient;
        this.objectMapper = objectMapper;
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
