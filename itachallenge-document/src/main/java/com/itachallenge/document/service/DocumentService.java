package com.itachallenge.document.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.document.proxy.IAuthClient;
import com.itachallenge.document.proxy.IChallengeClient;
import com.itachallenge.document.proxy.IScoreClient;
import com.itachallenge.document.proxy.IUserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    private final IChallengeClient challengeClient;
    private final IUserClient userClient;
    private final IAuthClient authClient;
    private final IScoreClient scoreClient;

    private final ObjectMapper objectMapper;

    public DocumentService(IChallengeClient challengeClient, IUserClient userClient1, IAuthClient authClient, IScoreClient scoreClient,
                           ObjectMapper objectMapper) {
        this.challengeClient = challengeClient;
        this.userClient = userClient1;
        this.authClient = authClient;
        this.scoreClient = scoreClient;
        this.objectMapper = objectMapper;
    }

    public String getSwaggerUserDocsStr() {
        return userClient.getSwaggerDocs();
    }
    public String getSwaggerChallengeDocsStr() {
        return challengeClient.getSwaggerDocs();
    }
    public String getSwaggerAuthDocsStr() { return authClient.getSwaggerDocs(); }
    public String getSwaggerScoreDocsStr() { return scoreClient.getSwaggerDocs(); }

}
