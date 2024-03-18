package com.itachallenge.challenge.service;

import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.StatisticsResponseDto;
import com.itachallenge.challenge.mqclient.ZMQClient;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
class ZMQClientServiceImplTest {

    @Mock
    private ZMQClient mockZMQClient;
    @InjectMocks
    private ZMQClientServiceImpl zmqClientService;

    @Before
    public void setup() {
        ZMQClientServiceImpl zMQClientService = new ZMQClientServiceImpl(mockZMQClient);
    }
    @Test
    public void testRequestUserData() {
        ZMQClientServiceImpl zmqClientService = new ZMQClientServiceImpl(mockZMQClient);


        ChallengeRequestDto expectedRequest = ChallengeRequestDto.builder()
                    .challengeId(UUID.randomUUID())
                    .build();
            // Arrange
            CompletableFuture<Object> completableFuture = new CompletableFuture<>();
            StatisticsResponseDto buildResult = StatisticsResponseDto.builder().bookmarks(1).percent(1).build();
            completableFuture.obtrudeValue(buildResult);
        Object zMQClient;
        when(zMQClient.sendMessage(Mockito.<Object>any(), Mockito.<Class<Object>>any())).thenReturn(completableFuture);

            // Act
        IZMQClientService zMQClientServiceImpl;
        zMQClientServiceImpl.requestUserData();

            // Assert
            verify(zMQClient).sendMessage(Mockito.<Object>any(), Mockito.<Class<Object>>any());
            // Verificar que se registren los mensajes adecuados en el logger
            // (por ejemplo, usando ArgumentCaptor para capturar los logs y luego verificar su contenido)
        }



    }
        /*ZMQClientServiceImpl zmqClientService = new ZMQClientServiceImpl(mockZMQClient);

        ChallengeRequestDto expectedRequest = ChallengeRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .build();

        StatisticsResponseDto mockResponse = new StatisticsResponseDto();
        mockResponse.setPercent(75);
        mockResponse.setBookmarks(10);

        CompletableFuture<Object> mockFuture = CompletableFuture.completedFuture(mockResponse);

        when(mockZMQClient.sendMessage(eq(expectedRequest), eq(StatisticsResponseDto.class))).thenReturn(mockFuture);

        zmqClientService.requestUserData();

        // Verificar que se haya llamado a sendMessage con el objeto ChallengeRequestDto correcto
        verify(mockZMQClient).sendMessage(eq(expectedRequest), eq(StatisticsResponseDto.class));
    }*/
}