package com.itachallenge.score.mqclient;

import com.itachallenge.score.helper.ObjectSerializer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

public class ScoreMicroZmqClientTest {

    @Mock
    private ZContext zContext;

    @Mock
    private ZMQ.Socket zmqSocket;

    @Mock
    private ObjectSerializer objectSerializer;

    @InjectMocks
    private ScoreMicroZmqClient scoreMicroZmqClient;

    private static final String FILE_LOCATION = "/path/to/store/testparams";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scoreMicroZmqClient = new ScoreMicroZmqClient(FILE_LOCATION);
        when(zContext.createSocket(ZMQ.REQ)).thenReturn(zmqSocket);
    }

    @Test
    void requestTestParams_shouldWriteTestParamsToFile() throws Exception {
        // Arrange
        String challengeId = "challenge123";
        String languageId = "language456";
        String requestMessage = "GET_TEST_PARAMS " + challengeId + " " + languageId;

        // Mocking the behavior of ZMQ socket
        when(zmqSocket.recv(0)).thenReturn("mocked_response".getBytes(StandardCharsets.UTF_8));

        // Mocking the deserialization of response into a Map
        Map<String, Object> mockTestParams = new HashMap<>();
        mockTestParams.put("input", "[5,7]");
        mockTestParams.put("expectedOutput", "12");
        when(objectSerializer.deserialize(any(byte[].class), eq(Map.class))).thenReturn(mockTestParams);

        // Act
        scoreMicroZmqClient.requestTestParams(challengeId, languageId).join();

        // Assert that FileUtils.writeStringToFile() was called with correct arguments
        File expectedFile = new File(FILE_LOCATION + "/test_params.txt");
        verifyStatic(FileUtils.class);
        FileUtils.writeStringToFile(eq(expectedFile), anyString(), eq(StandardCharsets.UTF_8));

        // Verify that ZMQ socket sent the correct message
        verify(zmqSocket).send(requestMessage.getBytes(StandardCharsets.UTF_8), 0);

        // Verify that ZMQ socket received a response
        verify(zmqSocket).recv(0);

        // Verify that the response was deserialized correctly
        verify(objectSerializer).deserialize(any(byte[].class), eq(Map.class));
    }
}
