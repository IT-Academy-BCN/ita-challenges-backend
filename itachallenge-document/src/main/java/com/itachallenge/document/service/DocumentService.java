package com.itachallenge.document.service;

import com.itachallenge.document.proxy.*;
import org.springframework.stereotype.Service;


@Service
public class DocumentService implements IDocumentService{
    private final IChallengeClient challengeClient;
    private final IUserClient userClient;
    private final IAuthClient authClient;
    private final IScoreClient scoreClient;

    public DocumentService(IChallengeClient challengeClient,
                           IUserClient userClient1,
                           IAuthClient authClient,
                           IScoreClient scoreClient) {
        this.challengeClient = challengeClient;
        this.userClient = userClient1;
        this.authClient = authClient;
        this.scoreClient = scoreClient;
    }

    @Override
    public String getSwaggerUserDocsStr() {
        return userClient.getSwaggerDocs();
    }
    @Override
    public String getSwaggerChallengeDocsStr() {
        return challengeClient.getSwaggerDocs();
    }
    @Override
    public String getSwaggerAuthDocsStr() {
        return authClient.getSwaggerDocs();
    }
    @Override
    public String getSwaggerScoreDocsStr() {
        return scoreClient.getSwaggerDocs();
    }
    @Override
    public String getSwaggerDefaultDocsStr(String apiName) { return DefaultApi.getDefaultApi(apiName);}
}
