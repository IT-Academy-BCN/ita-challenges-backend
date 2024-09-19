package com.itachallenge.score.sandbox;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.service.CodeProcessingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CodeProcessingManagerTest {

    @Mock
    private Filter filterChain;

    @Mock
    private JavaSandboxContainer javaSandboxContainer;

    @InjectMocks
    private CodeProcessingManager codeProcessingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Test processCode successful")
    @Test
    void testProcessCodeSuccessful() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1;"
        );

        ExecutionResultDto executionResultDto = new ExecutionResultDto();
        executionResultDto.setCompiled(true);
        executionResultDto.setExecution(true);
        executionResultDto.setMessage("5432");

        when(filterChain.apply(any(String.class), any(String.class))).thenReturn(executionResultDto);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("5432", responseEntity.getBody().getCompilationMessage());
        verify(javaSandboxContainer, times(1)).stopContainer();
    }

    @DisplayName("Test processCode compilation failure")
    @Test
    void testProcessCodeCompilationFailure() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1," // Code that will cause compilation/runtime error because it doesn't have ";" at the end
        );

        ExecutionResultDto executionResultDto = new ExecutionResultDto();
        executionResultDto.setCompiled(false);
        executionResultDto.setMessage("Compilation failed");

        when(filterChain.apply(any(String.class), any(String.class))).thenReturn(executionResultDto);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().getScore());
        assertEquals("Compilation failed", responseEntity.getBody().getCompilationMessage());
        verify(javaSandboxContainer, times(1)).stopContainer();
    }
}