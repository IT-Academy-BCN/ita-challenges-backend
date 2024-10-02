// CodeProcessingManagerTest.java
package com.itachallenge.score.service;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.sandbox.DockerExecutor;
import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.filter.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Testcontainers
@ExtendWith(MockitoExtension.class)
class CodeProcessingManagerTest {

    @Mock
    private Filter filterChain;

    @Mock
    private DockerExecutor dockerExecutor;

    @InjectMocks
    private CodeProcessingManager codeProcessingManager;

    @Container
    private GenericContainer<?> javaExecutorContainer = new GenericContainer<>("java-executor")
            .withExposedPorts(8080);

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
            """; // result should be [1, 1, 3, 4, 5, 9]
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
        when(dockerExecutor.executeDockerCommand(any(String.class))).thenReturn(executionResult);

        ResponseEntity<ScoreResponse> responseEntity = codeProcessingManager.processCode(scoreRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(100, responseEntity.getBody().getScore());
        assertEquals("Code compiled and executed, and result match: 5432", responseEntity.getBody().getCompilationMessage());
    }

    // Other test methods...
}