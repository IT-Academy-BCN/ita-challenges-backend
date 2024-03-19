package com.itachallenge.user.service;

import com.itachallenge.challenge.mqclient.ZMQClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ZMQServerServiceImplTest {
    @Mock
    private ZMQClient zmqClient;
    @InjectMocks
    private ZMQServerServiceImpl zmqServerService;

    @BeforeEach
    void setUp() {
        // Inicializar la instancia de ZMQServerServiceImpl con el mock de zmqClient
        zmqServerService = new ZMQServerServiceImpl(zmqClient);
    }

    @Test
    public void testRespondToChallengeRequest_validChallengeId() {
        UUID challengeId = UUID.randomUUID();

        // Configurar el comportamiento del mock
        Mockito.when(zmqClient.sendMessage(Mockito.any(), Mockito.any())).thenReturn(CompletableFuture.completedFuture("Success"));

        CompletableFuture<Object> response = zmqServerService.respondToChallengeRequest(challengeId);

        assertNotNull(response);
    }

    @Test
    public void testRespondToChallengeRequest_responseReceived() {
        UUID challengeId = UUID.randomUUID();

        // Configurar el comportamiento del mock
        CompletableFuture<Object> mockResponse = CompletableFuture.completedFuture("Mock Response");
        Mockito.when(zmqClient.sendMessage(Mockito.any(), Mockito.any())).thenReturn(mockResponse);

        CompletableFuture<Object> response = zmqServerService.respondToChallengeRequest(challengeId);

        assertEquals(mockResponse, response);
    }
}