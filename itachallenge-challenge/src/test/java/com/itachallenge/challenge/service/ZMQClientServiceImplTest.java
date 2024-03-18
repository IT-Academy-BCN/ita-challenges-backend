package com.itachallenge.challenge.service;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.itachallenge.challenge.dto.zmq.ChallengeRequestDto;
import com.itachallenge.challenge.dto.zmq.StatisticsResponseDto;
import com.itachallenge.challenge.mqclient.ZMQClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZMQClientServiceImplTest {

    @Mock
    private ZMQClient mockZMQClient;
    @InjectMocks
    private ZMQClientServiceImpl zmqClientService;

    @BeforeEach
    public void setup() {

        zmqClientService = new ZMQClientServiceImpl(mockZMQClient);
    }

    @Test
    public void testRequestUserData() {
        // Arrange
        ChallengeRequestDto expectedRequest = ChallengeRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .build();
        StatisticsResponseDto expectedResponse = StatisticsResponseDto.builder()
                .bookmarks(1)
                .percent(1)
                .build();

        when(mockZMQClient.sendMessage(any(), eq(StatisticsResponseDto.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedResponse));

        // Capture logs
        Logger logger = LoggerFactory.getLogger(ZMQClientServiceImpl.class);
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        lc.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(listAppender);

        // Act
        zmqClientService.requestUserData();

        // Assert
        verify(mockZMQClient).sendMessage(any(), eq(StatisticsResponseDto.class));

        // Check logs
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());
        assertEquals("Percentage: 1", logsList.get(0).getFormattedMessage());
        assertEquals("Bookmarks: 1", logsList.get(1).getFormattedMessage());
       }


        /*
        // Arrange
        ChallengeRequestDto expectedRequest = ChallengeRequestDto.builder()
                .challengeId(UUID.randomUUID())
                .build();
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        StatisticsResponseDto buildResult = StatisticsResponseDto.builder().bookmarks(1).percent(1).build();
        completableFuture.obtrudeValue(buildResult);

        when(mockZMQClient.sendMessage(any(), eq(StatisticsResponseDto.class)))
                .thenReturn(completableFuture);

        // Act
        zmqClientService.requestUserData();

        // Assert
        verify(mockZMQClient).sendMessage(any(), eq(StatisticsResponseDto.class));
        // Verificar que se registren los mensajes adecuados en el logger
        // (por ejemplo, usando ArgumentCaptor para capturar los logs y luego verificar su contenido)
    }*/

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
