package com.itachallenge.document.service;

import com.itachallenge.document.proxy.IChallengeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DocumentService {

    @Autowired
    private IChallengeClient challengeClient;

    public DocumentService(IChallengeClient challengeClient) {
        this.challengeClient = challengeClient;
    }

    public Mono<String> getSwaggerDocs() {
        return challengeClient.getSwaggerDocs();
    }
}
