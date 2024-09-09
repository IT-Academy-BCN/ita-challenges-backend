package com.itachallenge.score.sandBox;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandBox.sandBoxContainer.JavaSandboxContainer;
import com.itachallenge.score.sandBox.sandBox_filter.Filter;
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

class CodeExecutionManagerTest {

    @Mock
    private Filter filterChain;

    @Mock
    private JavaSandboxContainer javaSandboxContainer;

    @Mock
    private CodeExecutionService codeExecutionService;

    @InjectMocks
    private CodeExecutionManager codeExecutionManager;

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
        executionResultDto.setMessage("Compilation successful");

        when(filterChain.apply(any(String.class), any(String.class))).thenReturn(executionResultDto);
        when(codeExecutionService.calculateScore(any(ExecutionResultDto.class))).thenReturn(100);

        ResponseEntity<ScoreResponse> responseEntity = codeExecutionManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Compilation successful", responseEntity.getBody().getCompilationMessage());
        verify(javaSandboxContainer, times(1)).stopContainer();
    }

    @DisplayName("Test processCode compilation failure")
    @Test
    void testProcessCodeCompilationFailure() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1," // Code that will cause compilation/runtime error because don't have ";" at the end
        );

        ExecutionResultDto executionResultDto = new ExecutionResultDto();
        executionResultDto.setCompiled(false);
        executionResultDto.setMessage("Compilation failed");

        when(filterChain.apply(any(String.class), any(String.class))).thenReturn(executionResultDto);

        ResponseEntity<ScoreResponse> responseEntity = codeExecutionManager.processCode(scoreRequest);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().getScore());
        assertEquals("Compilation failed", responseEntity.getBody().getCompilationMessage());
        verify(javaSandboxContainer, times(1)).stopContainer();
    }
}