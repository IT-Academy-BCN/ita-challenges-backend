package com.itachallenge.score.service;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.sandbox.CompileExecuter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CodeProcessingManagerTest {

    private static final Logger log = LoggerFactory.getLogger(CodeProcessingManagerTest.class);

    @Mock
    private Filter filterChain;

    @Mock
    private CompileExecuter compileExecuter;

    @InjectMocks
    private CodeProcessingManager codeProcessingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Code that will be used in the tests. Bubble sort algorithm
    String codeToCompile = """
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
            """; //result should be [1, 1, 3, 4, 5, 9]

    @DisplayName("Test processCode successful")
    @Test
    void testProcessCodeSuccessful() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(true);
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("5432");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(compileExecuter.executeCode(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        log.info("Response entity: {}", responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, and result match: 5432", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test processCode compilation failure")
    @Test
    void testProcessCodeCompilationFailure() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "int number = 1," // Code that will cause compilation/runtime error because it doesn't have ";" at the end
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(false);
        executionResult.setMessage("Compilation failed");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().getScore());
        assertEquals("Compilation failed", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test processCode execution failure")
    @Test
    void testProcessCodeExecutionFailure() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(false);
        executionResult.setMessage("Execution failed");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(compileExecuter.executeCode(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(25, responseEntity.getBody().getScore());
        assertEquals("Execution error: Execution failed", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test processCode partial match")
    @Test
    void testProcessCodePartialMatch() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("-543298"); // Partial match

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(compileExecuter.executeCode(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(75, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, and result partially match: -543298", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test processCode no match")
    @Test
    void testProcessCodeNoMatch() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("[9, 5, 4, 3, 1, 1]"); // No match

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);
        when(compileExecuter.executeCode(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(50, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, but result doesn't match: [9, 5, 4, 3, 1, 1]", responseEntity.getBody().getCompilationMessage());
    }

    @DisplayName("Test filterChain passes all filters")
    @Test
    void testFilterChainPassesAllFilters() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(true);
        executionResult.setCompiled(true);
        executionResult.setExecution(true);
        executionResult.setMessage("5432");

        when(filterChain.apply(codeToCompile)).thenReturn(executionResult);
        when(compileExecuter.executeCode(codeToCompile)).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        log.info("Response entity: {}", responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, and result match: 5432", responseEntity.getBody().getCompilationMessage());
    }


    @DisplayName("Test filterChain fails a filter")
    @Test
    void testFilterChainFailsFilter() {
        ScoreRequest scoreRequest = new ScoreRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                codeToCompile
        );

        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setSuccess(false);
        executionResult.setMessage("Filter failed");

        when(filterChain.apply(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        log.info("Response entity: {}", responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().getScore());
        assertEquals("Filter failed", responseEntity.getBody().getCompilationMessage());
    }
}