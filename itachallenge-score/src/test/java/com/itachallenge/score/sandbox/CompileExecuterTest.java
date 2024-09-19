package com.itachallenge.score.sandbox;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.service.CodeProcessingManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
public class CompileExecuterTest {

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
        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeResultNotMatch() {
        String sourceCode = "System.out.println(\"Bad Hello, World!\");";
        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeCompilationError() {
        String sourceCode = "System.out.println(\"Hello, World!\"";  // Missing closing parenthesis
        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode);

        Assertions.assertFalse(resultDto.isCompiled());
        assertTrue(resultDto.getMessage().contains("error"));
    }

    @Test
    void testCompileAndRunCodeExecutionError() {
        String sourceCode = "int num = 10; int div = 0; System.out.println(num / div);";  // Division by zero
        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
        assertTrue(resultDto.getMessage().contains("Exception"));
    }

    @Test
    void testCompileAndRunCodeResultNotEqual() {
        String sourceCode = "System.out.println(\"Goodbye, World!\");";
        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.getMessage().equals("Hello, World!"));
    }
}