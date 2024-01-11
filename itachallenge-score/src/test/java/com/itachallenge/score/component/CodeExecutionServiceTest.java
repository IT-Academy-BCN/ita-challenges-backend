package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CodeExecutionServiceTest {
    @Autowired
    CodeExecutionService codeExecutionService;

    String[] args = new String[]{};

    @Test
    public void testCompileAndRunCode() {

        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    public void testCompileAndRunCodeResultNotMatch() {

        String sourceCode = "System.out.println(\"Bad Hello, World!\");\n";

        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    @Test
    public void testCompileAndRunCodeCompilationError() {

        String sourceCode = "System.out.println(\"Hello, World!\")\n";  //falta ;
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertFalse(resultDto.isCompile());
        //verificar que resultDto.getMeassage empieze por "Compilation failed: "
        Assertions.assertTrue(resultDto.getMessage().startsWith("Compilation failed: "));
    }

    @Test
    public void testCompileAndRunCodeExecutionError() {

        String sourceCode =
                        "        int num = 10;\n" +
                        "        int div = 0;\n" +
                        "        System.out.println(num / div);\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: "));
    }

    @Test
    public void testCompileAndRunCodeWithParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                "System.out.println(num / 2);\n";
        String codeResult = "5\n";  // Esperamos que 10 / 2 sea 5
        args = new String[]{"10"};  // Pasamos 10 como argumento al método main

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    public void testCompileAndRunCodeWithWrongTypeParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                "System.out.println(num / 2);\n";
        String codeResult = "5\n";  // Esperamos que 10 / 2 sea 5
        args = new String[]{"a, bce"};  // Pasamos tipo erróneo para int como argumento al método main

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: java.lang.NumberFormatException: "));
    }

    //Add test for ClassNotFoundException


    //Add test for ClassNotFoundException | NoSuchMethodException | IllegalAccessException | ¿InvocationTargetException?

}
