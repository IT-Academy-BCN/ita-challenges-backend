package com.itachallenge.user.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IZMQServerService {
    CompletableFuture<Object> respondToChallengeRequest(UUID challengeId);
}
