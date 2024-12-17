package com.itachallenge.score.mqclient;

import com.itachallenge.score.dto.zmq.TestParamsRequestDto;
import com.itachallenge.score.dto.zmq.TestParamsResponseDto;
import com.itachallenge.score.util.FileUtil;
import com.itachallenge.score.util.ObjectSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZMQClientTest {

    @Mock
    private ZContext mockContext;

    @Mock
    private FileUtil mockFileUtil;

    @Mock
    private ObjectSerializer mockObjectSerializer;

    private ZMQClient zmqClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        zmqClient = new ZMQClient(mockContext, "tcp://localhost:5555", mockFileUtil, mockObjectSerializer);
    }

    @Test
    void testRequestTestParams_SuccessfulExecution() throws Exception {
        UUID challengeId = UUID.randomUUID();
        UUID solutionId = UUID.randomUUID();

        // Mock serialization and deserialization
        //TestParamsRequestDto requestDto = new TestParamsRequestDto();
        when(mockObjectSerializer.serialize(any(TestParamsRequestDto.class))).thenReturn("serialized_data".getBytes());

        TestParamsResponseDto responseDto = mock(TestParamsResponseDto.class);;
        when(mockObjectSerializer.deserialize(any(byte[].class), eq(TestParamsResponseDto.class))).thenReturn(responseDto);

        when(responseDto.toParamList()).thenReturn(List.of("param1=value1", "param2=value2"));

        // Mock socket behavior
        ZMQ.Socket mockSocket = mock(ZMQ.Socket.class);
        when(mockContext.createSocket(SocketType.REQ)).thenReturn(mockSocket);

        when(mockSocket.recv(0)).thenReturn("response_data".getBytes());

        zmqClient.requestTestParams(challengeId, solutionId).join();

        // Verify interactions
        verify(mockObjectSerializer).serialize(any(TestParamsRequestDto.class));
        verify(mockSocket).send(any(byte[].class), eq(0));

        verify(mockObjectSerializer).deserialize(any(byte[].class), eq(TestParamsResponseDto.class));

        verify(mockFileUtil).createTestParamsFile(eq(List.of("param1=value1", "param2=value2")), eq(solutionId));

        verify(mockSocket).close(); // Ensure socket is closed after use
    }

    @Test
    void testRequestTestParams_EmptyResponse() throws Exception {
        UUID challengeId = UUID.randomUUID();
        UUID solutionId = UUID.randomUUID();

        ZMQ.Socket mockSocket = mock(ZMQ.Socket.class);

        when(mockContext.createSocket(SocketType.REQ)).thenReturn(mockSocket);

        when(mockSocket.recv(0)).thenReturn(null);

        zmqClient.requestTestParams(challengeId, solutionId).join();

        verifyNoInteractions(mockFileUtil);
        verify(mockSocket).close();
    }
}
