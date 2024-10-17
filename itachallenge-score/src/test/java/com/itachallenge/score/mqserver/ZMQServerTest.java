package com.itachallenge.score.mqserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itachallenge.score.dto.zmq.ScoreRequestDto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import com.itachallenge.score.helper.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQServerTest {
//TODO: NO FUNCIONA
    @Mock
    private ZContext zContextMock;
    @Mock
    private ZMQ.Socket socketMock;
    @Mock
    private ObjectSerializer objectSerializerMock;
    @InjectMocks
    private ZMQServer zmqServer;
    private ScoreRequestDto scoreRequestDto;
    private ScoreResponseDto scoreResponseDto;
    private byte[] requestBytes;
    private byte[] responseBytes;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(zContextMock.createSocket(ZMQ.REP)).thenReturn(socketMock);

        scoreRequestDto = ScoreRequestDto.builder()
                .uuidChallenge(UUID.fromString("7fc6a737-dc36-4e1b-87f3-120d81c548aa"))
                .uuidLanguage(UUID.fromString("1e047ea2-b787-49e7-acea-d79e92be3909"))
                .solutionText("Solution Text Test")
                .build();

        scoreResponseDto = ScoreResponseDto.builder()
                .uuidChallenge(scoreRequestDto.getUuidChallenge())
                .uuidLanguage(scoreRequestDto.getUuidLanguage())
                .solutionText(scoreRequestDto.getSolutionText())
                .score(99)
                .errors("xxx")
                .build();

        requestBytes = objectSerializerMock.serialize(scoreRequestDto);
        responseBytes = objectSerializerMock.serialize(scoreResponseDto);
    }

    @Test
    void testRun() throws Exception {

        when(socketMock.recv(0))
                .thenReturn(requestBytes)
                .thenReturn(null);

        when(objectSerializerMock.deserialize(requestBytes, ScoreRequestDto.class))
                .thenReturn(scoreRequestDto);
        when(objectSerializerMock.serialize(any(ScoreResponseDto.class))).thenReturn(responseBytes);

        Thread serverThread = new Thread(() -> zmqServer.run());
        serverThread.start();

        Thread.sleep(500);

        verify(socketMock).recv(0);
        verify(socketMock).send(responseBytes, 0);

        serverThread.interrupt();
        serverThread.join();
    }
}