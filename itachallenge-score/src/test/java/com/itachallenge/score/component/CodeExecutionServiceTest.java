package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;

@SpringBootTest
public class CodeExecutionServiceTest {
    @Autowired
    CodeExecutionService codeExecutionService;

    //Test for client code result match
    @Test
    public void testCompileAndRunCode() {

        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    //Test for client code result not match
    @Test
    public void testCompileAndRunCodeResultNotMatch() {

        String sourceCode = "System.out.println(\"Bad Hello, World!\");\n";

        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    //Test for client code compilation error
    @Test
    public void testCompileAndRunCodeCompilationError() {

        String sourceCode = "System.out.println(\"Hello, World!\")\n";  //falta ;
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertFalse(resultDto.isCompile());
        //verificar que resultDto.getMeassage empieze por "Compilation failed: "
        Assertions.assertTrue(resultDto.getMessage().startsWith("Compilation failed: "));
    }

    //Test for client code execution error
    @Test
    public void testCompileAndRunCodeExecutionError() {

        String sourceCode =
                        "        int num = 10;\n" +
                        "        int div = 0;\n" +
                        "        System.out.println(num / div);\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: "));
    }

    //Test for client code correct parameter injection
    @Test
    public void testCompileAndRunCodeWithParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5";  // Esperamos que 10 / 2 sea 5

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult,10);// Pasamos 10 como argumento al método main

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    //Test for client code wrong parameter injection
    @Test
    public void testCompileAndRunCodeWithWrongTypeParameterInjection() {
        String sourceCode =
                "int num = Integer.parseInt(args[0]);\n" +
                        "System.out.println(num / 2);\n";
        String codeResult = "5";  // Esperamos que 10 / 2 sea 5
        Object args = new String[]{"a, bce"};  // Pasamos tipo erróneo para int como argumento al método main
        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, args);

        // Verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: java.lang.NumberFormatException: "));
    }
    @Test
    public void testCompileAndRunCodeWithNullTypeParameterInjection() {
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
    public void testCompileAndRunCodeClassNotFoundException() {
        String sourceCode = "public class NotMain {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompile());
        Assertions.assertThrows(ClassNotFoundException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main");
        });
    }

    //Test for NoSuchMethodException
    @Test
    public void testCompileAndRunCodeNoSuchMethodException() {
        String sourceCode = "public class Main {\n" +
                "    public static void notMain(String[] args) {\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompile());
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class);
        });
    }

    //Test for InvocationTargetException
    @Test
    public void testCompileAndRunCodeInvocationTargetException() {
        String sourceCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        throw new RuntimeException(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        String codeResult = "Hello, World!";

        String[] args = new String[]{};

        CompilationResult compilationResult = codeExecutionService.compile(sourceCode);
        codeExecutionService.execute(compilationResult, codeResult, args);

        Assertions.assertTrue(compilationResult.getExecutionResultDto().isCompile());
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            compilationResult.getCompiler().getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) args);
        });

    }

}
