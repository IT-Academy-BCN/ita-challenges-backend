package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class ZMQServerTest {

    @Mock
    private ObjectSerializer objectSerializer;

    @InjectMocks
    private ZMQServer zmqServer;

    private AutoCloseable closeable;


    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        zmqServer = new ZMQServer("tcp://localhost:5555");
        zmqServer.init(); // Initialize the server
    }

    @AfterEach
    public void tearDown() throws Exception {
        zmqServer.cleanup(); // Limpia el servidor
        closeable.close();
    }

    @Test
    public void serverStartsSuccessfully() {
        assertTrue(zmqServer.isRunning());
    }

    @Test
    public void serverHandlesValidRequest() throws Exception {
        ScoreRequestDto requestDto = ScoreRequestDto.builder()
                .uuidChallenge(UUID.randomUUID())
                .uuidLanguage(UUID.randomUUID())
                .solutionText("test-solution")
                .build();

        byte[] serializedRequest = objectSerializer.serialize(requestDto);
        when(objectSerializer.deserialize(any(byte[].class), eq(ScoreRequestDto.class))).thenReturn(requestDto);

        ScoreResponseDto expectedResponse = ScoreResponseDto.builder()
                .uuidChallenge(requestDto.getUuidChallenge())
                .uuidLanguage(requestDto.getUuidLanguage())
                .solutionText(requestDto.getSolutionText())
                .score(100)
                .errors(null)
                .compilationMessage("Success")
                .expectedResult("Expected Result")
                .build();

        byte[] serializedResponse = objectSerializer.serialize(expectedResponse);
        when(objectSerializer.serialize(any(ScoreResponseDto.class))).thenReturn(serializedResponse);

        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555");

            socket.send(serializedRequest);
            byte[] reply = socket.recv(5000);

            assertArrayEquals(serializedResponse, reply);
        }
    }

    @Test
    public void serverHandlesInvalidRequest() throws Exception {
        byte[] invalidRequest = "invalid-request".getBytes();
        when(objectSerializer.deserialize(any(byte[].class), eq(ScoreRequestDto.class))).thenThrow(new IOException("Deserialization error"));

        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(ZMQ.REQ);
            socket.connect("tcp://localhost:5555");

            socket.send(invalidRequest);
            byte[] reply = socket.recv(5000);

            assertArrayEquals(new byte[0], reply);
        }
    }

    @Test
    public void serverStopsSuccessfully() {
        zmqServer.cleanup();
        assertFalse(zmqServer.isRunning());
    }
}