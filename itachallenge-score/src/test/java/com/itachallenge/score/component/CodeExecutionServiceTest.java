package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CodeExecutionServiceTest {

    @Autowired
    private CodeExecutionService codeExecutionService;


    //Test for client code result match
    @Test
    void testCompileAndRunCode() {

        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    //Test for client code result not match
    @Test
    void testCompileAndRunCodeResultNotMatch() {

        String sourceCode = "System.out.println(\"Bad Hello, World!\");\n";

        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    //Test for client code compilation error
    @Test
    void testCompileAndRunCodeCompilationError() {

        String sourceCode = "System.out.println(\"Hello, World!\")\n";  //falta
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertFalse(resultDto.isCompiled());
        //verificar que resultDto.getMeassage empieze por "Compilation failed: "
        Assertions.assertTrue(resultDto.getMessage().startsWith("Compilation failed: "));
    }

    //Test for client code execution error
    @Test
    void testCompileAndRunCodeExecutionError() {

        String sourceCode =
                "        int num = 10;\n" +
                        "        int div = 0;\n" +
                        "        System.out.println(num / div);\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: "));
    }

    //Test for client code correct parameter injection
    @Test
    void testCompileAndRunCodeWithParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5";  // Esperamos que 10 / 2 sea 5

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 10);// Pasamos 10 como argumento al método main

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    //Test for client code wrong parameter injection
    @Test
    void testCompileAndRunCodeWithWrongTypeParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5";  // Esperamos que 10 / 2 sea 5
        Object args = new String[]{"a, bce"};  // Pasamos tipo erróneo para int como argumento al método main
        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
    }

    @Test
    void testCompileAndRunCodeWithNullTypeParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5";  // Esperamos que 10 / 2 sea 5
        Object nullObject = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            codeExecutionService.compileAndRunCode(sourceCode, codeResult, nullObject);
        });
    }

    //Test for ClassNotFoundException
    @Test
    void testCompileAndRunCodeClassNotFoundException() {
        String sourceCode = "public class NotMain {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompiled());
        Assertions.assertThrows(ClassNotFoundException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main");
        });
    }

    //Test for NoSuchMethodException
    @Test
    void testCompileAndRunCodeNoSuchMethodException() {
        String sourceCode = "public class Main {\n" +
                "    public static void notMain(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompiled());
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class);
        });
    }

    //Test for InvocationTargetException
    @Test
    void testCompileAndRunCodeInvocationTargetException() {
        String sourceCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        throw new RuntimeException(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompiled());
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) args);
        });

    }

    // Prueba de inyección de múltiples parámetros
    @Test
    void testCompileAndRunCodeWithMultipleParameterInjection() {
        String sourceCode =
                "int num1 = Integer.parseInt(args[0]);\n" +
                        "int num2 = Integer.parseInt(args[1]);\n" +
                        "System.out.println(num1 + num2);\n";
        String codeResult = "30";  // Esperamos que 10 + 20 sea 30

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 10, 20); // Pasamos 10 y 20 como argumentos al método main

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    // Prueba de inyección de parámetros de diferentes tipos
    @Test
    void testCompileAndRunCodeWithDifferentTypeParameterInjection() {
        String sourceCode =
                "double num = Double.parseDouble(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5.0";  // Esperamos que 10.0 / 2 sea 5.0

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 10.0); // Pasamos 10.0 como argumento al método main

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompiled());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    void testCalculateScore() {


        // Test case 1: Code compiled, executed, and result matched
        ExecutionResultDto result1 = new ExecutionResultDto(true, true, true, "");
        int score1 = codeExecutionService.calculateScore(result1);
        assertEquals(99, score1);

        // Test case 2: Code compiled and executed, but result did not match
        ExecutionResultDto result2 = new ExecutionResultDto(true, true, false, "");
        int score2 = codeExecutionService.calculateScore(result2);
        assertEquals(75, score2);

        // Test case 3: Code compiled, but did not execute
        ExecutionResultDto result3 = new ExecutionResultDto(true, false, false, "");
        int score3 = codeExecutionService.calculateScore(result3);
        assertEquals(50, score3);

        // Test case 4: Code did not compile
        ExecutionResultDto result4 = new ExecutionResultDto(false, false, false, "");
        int score4 = codeExecutionService.calculateScore(result4);
        assertEquals(0, score4);
    }

}
