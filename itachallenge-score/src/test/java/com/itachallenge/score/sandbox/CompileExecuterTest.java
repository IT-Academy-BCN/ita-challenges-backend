package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.service.CodeProcessingManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class CompileExecuterTest {

    @Autowired
    private CompileExecuter compileExecuter;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Autowired
    private CodeProcessingManager codeProcessingManager;

    @BeforeEach
    void setUp() {
        javaSandboxContainer = new JavaSandboxContainer();
    }

    @AfterEach
    void tearDown() {
        javaSandboxContainer.stopContainer();
    }

    @Test
    void testCompileAndRunCode() {
        String sourceCode = "System.out.println(\"Hello, World!\");";
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeResultNotMatch() {
        String sourceCode = "System.out.println(\"Bad Hello, World!\");";
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertFalse(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeCompilationError() {
        String sourceCode = "System.out.println(\"Hello, World!\"";  // Missing closing parenthesis
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertFalse(resultDto.isCompiled());
        assertTrue(resultDto.getMessage().contains("error"));
    }

    @Test
    void testCompileAndRunCodeExecutionError() {
        String sourceCode = "int num = 10; int div = 0; System.out.println(num / div);";  // Division by zero
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertFalse(resultDto.isExecution());
        assertTrue(resultDto.getMessage().contains("Exception"));
    }

    @Test
    void testCompileAndRunCodeResultNotEqual() {
        String sourceCode = "System.out.println(\"Goodbye, World!\");";
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertNotEquals("Hello, World!", resultDto.getMessage());
    }

    @Test
    void testExecutionErrorHandling() {
        String sourceCode = "public class Main { public static void main(String[] args) { throw new RuntimeException(\"Test Exception\"); } }";
        ExecutionResult resultDto = compileExecuter.executeCode(sourceCode);

        assertFalse(resultDto.isCompiled());
        assertFalse(resultDto.isExecution());
        assertTrue(resultDto.getMessage().contains("Exception"));
    }
}