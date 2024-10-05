package com.itachallenge.score.service;

import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.exception.DockerExecutionException;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.sandbox.DockerExecutor;
import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeProcessingManagerTest {

    @Mock
    private Filter filterChain;

    @Mock
    private DockerExecutor dockerExecutor;

    @InjectMocks
    private CodeProcessingManager codeProcessingManager;

    private String codeToCompile;

    @BeforeEach
    void setUp() {
        codeToCompile = """
                public class Main {
                    public static void main(String[] args) {
                        int[] numArray = {3, 1, 4, 1, 5, 9};
                        int n = numArray.length;
                        for (int i = 0; i < n-1; i++) {
                            for (int j = 0; j < n-i-1; j++) {
                                if (numArray[j] > numArray[j+1]) {
                                    int temp = numArray[j];
                                    numArray[j] = numArray[j+1];
                                    numArray[j+1] = temp;
                                }
                            }
                        }
                        System.out.println(Arrays.toString(numArray));
                    }
                }
                """;
    }

    @DisplayName("Test processCode successful")
    @Test
    void testProcessCodeSuccessful() throws IOException, InterruptedException {
        ScoreRequest scoreRequest = new ScoreRequest(UUID.randomUUID(), UUID.randomUUID(), codeToCompile);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(true);
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("5432");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(dockerExecutor.execute(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, and result match: 5432", responseEntity.getBody().getCompilationMessage());
    }


    @DisplayName("Test processCode with InterruptedException")
    @Test
    void testProcessCodeWithInterruptedException() throws IOException, InterruptedException {
        ScoreRequest scoreRequest = new ScoreRequest(UUID.randomUUID(), UUID.randomUUID(), codeToCompile);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(true);

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(dockerExecutor.execute(any(String.class))).thenThrow(new InterruptedException("Execution interrupted"));

        assertThrows(DockerExecutionException.class, () -> {
            codeProcessingManager.processCode(scoreRequest);
        });
    }

    @DisplayName("Test calculateScore with compilation error")
    @Test
    void testCalculateScoreWithCompilationError() {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(false);
        executionResult.setMessage("Compilation error");

        int score = codeProcessingManager.calculateScore(executionResult, "5432");

        assertEquals(0, score);
        assertEquals("Compilation error", executionResult.getMessage());
    }

    @DisplayName("Test calculateScore with execution error")
    @Test
    void testCalculateScoreWithExecutionError() {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(false);
        executionResult.setMessage("Execution error");

        int score = codeProcessingManager.calculateScore(executionResult, "5432");

        assertEquals(25, score);
        assertEquals("Execution error: Execution error", executionResult.getMessage());
    }

    @DisplayName("Test calculateScore with partial match")
    @Test
    void testCalculateScoreWithPartialMatch() {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("54321");

        int score = codeProcessingManager.calculateScore(executionResult, "5432");

        assertEquals(75, score);
        assertEquals("Code compiled and executed, and result partially match: 54321", executionResult.getMessage());
    }

    @DisplayName("Test calculateScore with no match")
    @Test
    void testCalculateScoreWithNoMatch() {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("1234");

        int score = codeProcessingManager.calculateScore(executionResult, "5432");

        assertEquals(50, score);
        assertEquals("Code compiled and executed, but result doesn't match: 1234", executionResult.getMessage());
    }

    @DisplayName("Test processCode with filter failure")
    @Test
    void testProcessCodeWithFilterFailure() {
        ScoreRequest scoreRequest = new ScoreRequest(UUID.randomUUID(), UUID.randomUUID(), codeToCompile);

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(false);
        executionResult.setMessage("Filter failed");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().getScore());
        assertEquals("Filter failed", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test calculateScore with empty message")
    @Test
    void testCalculateScoreWithEmptyMessage() {
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(false);
        executionResult.setMessage("");

        int score = codeProcessingManager.calculateScore(executionResult, "5432");

        assertEquals(0, score);
        assertEquals("Compilation error: ", executionResult.getMessage());
    }
}