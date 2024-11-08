package com.itachallenge.score.mqserver;

import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@SpringBootTest
@PropertySource("classpath:application-test.yml")

class ZMQServerTest {

    private ZContext contextMock;
    private ZMQ.Socket socketMock;
    private ObjectSerializer objectSerializerMock;
    private ZMQServer zmqServer;

    @BeforeEach
    void setUp() {
        contextMock = mock(ZContext.class);
        socketMock = mock(ZMQ.Socket.class);
        objectSerializerMock = mock(ObjectSerializer.class);

        when(contextMock.createSocket(SocketType.REP)).thenReturn(socketMock);

        String socketAddress = "tcp://127.0.0.1:5555";
        zmqServer = new ZMQServer(contextMock, socketAddress, objectSerializerMock);
    }

    @AfterEach void tearDown() {
        if (zmqServer != null) {
            zmqServer.stop();
        }
    }

    @Test
    void testServerInitialization() throws InterruptedException {
        zmqServer.start();

        Thread.sleep(500);

        verify(contextMock, times(1)).createSocket(SocketType.REP);
        verify(socketMock, times(1)).bind("tcp://127.0.0.1:5555");

        zmqServer.stop();
    }

    @Test void testReceiveAndProcessMessage() throws Exception {
        byte[] messageBytes = "test message".getBytes();
        ScoreRequestDto requestDto = new ScoreRequestDto(UUID.randomUUID(), UUID.randomUUID(), "solutionText");
        ScoreResponseDto responseDto = ScoreResponseDto.builder()
                .uuidChallenge(requestDto.getUuidChallenge())
                .uuidLanguage(requestDto.getUuidLanguage())
                .solutionText(requestDto.getSolutionText())
                .score(99).errors("xxx")
                .compilationMessage("Compilation Message Text")
                .expectedResult("Expected Result Text")
                .build();
        byte[] responseBytes = "test response".getBytes();
        when(socketMock.recv(0)).thenReturn(messageBytes);
        when(objectSerializerMock.deserialize(messageBytes, ScoreRequestDto.class)).thenReturn(requestDto);
        when(objectSerializerMock.serialize(responseDto)).thenReturn(responseBytes);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return requestDto;
        }).when(objectSerializerMock).deserialize(messageBytes, ScoreRequestDto.class);

        zmqServer.start();

        latch.await();

        zmqServer.stop();

        verify(socketMock, times(1)).recv(0);
        verify(objectSerializerMock, times(1)).deserialize(messageBytes, ScoreRequestDto.class);
        verify(objectSerializerMock, times(1)).serialize(responseDto);
        verify(socketMock, times(1)).send(responseBytes, 0);
    }

    @Test
    void testServerStop() throws InterruptedException {
        zmqServer.start();

        Thread.sleep(500);

        zmqServer.stop();

        Thread.sleep(500);
        assertFalse(zmqServer.isRunning());
    }

    @Test
    void testErrorHandlingDuringDeserialization() throws InterruptedException, IOException {
        byte[] messageBytes = "test message".getBytes();

        when(socketMock.recv(0)).thenReturn(messageBytes);
        doThrow(new IOException("Deserialization failed")).when(objectSerializerMock).deserialize(messageBytes, ScoreRequestDto.class);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            throw new IOException("Deserialization failed");
        }).when(objectSerializerMock).deserialize(messageBytes, ScoreRequestDto.class);

        zmqServer.start();

        latch.await();

        zmqServer.stop();

        verify(socketMock, never()).send(any(byte[].class), anyInt());
        verify(objectSerializerMock, times(1)).deserialize(messageBytes, ScoreRequestDto.class);
    }


    @Test
    void testErrorHandlingDuringSerialization() throws InterruptedException, IOException {
        byte[] messageBytes = "test message".getBytes();
        ScoreRequestDto requestDto = new ScoreRequestDto(UUID.randomUUID(), UUID.randomUUID(), "solutionText");

        when(socketMock.recv(0)).thenReturn(messageBytes);
        when(objectSerializerMock.deserialize(messageBytes, ScoreRequestDto.class)).thenReturn(requestDto);

        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            throw new JsonProcessingException("Serialization failed") {};
        }).when(objectSerializerMock).serialize(any(ScoreResponseDto.class));

        zmqServer.start();

        latch.await();

        zmqServer.stop();

        verify(socketMock, never()).send(any(byte[].class), anyInt());
        verify(objectSerializerMock, times(1)).deserialize(messageBytes, ScoreRequestDto.class);
        verify(objectSerializerMock, times(1)).serialize(any(ScoreResponseDto.class));
    }
}