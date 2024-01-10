package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CodeExecutionServiceTest {
    @Autowired
    CodeExecutionService codeExecutionService;
    @Test
    public void testCompileAndRunCode() {

        String sourceCode =
                "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"Hello, World!\");\n" +
                        "         int i = 1;"+
                        "         int j = 2;"+
                        "         int k = i + j;"+
                        "         System.out.println(k);"+
                        "    }\n" +
                        "}";

        String codeResult = "Hello, World!\n" + "3\n";

        ExecutionResultDto resultDto =  codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    public void testCompileAndRunCodeResultNotMatch() {

        String sourceCode =
                "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"Bad Hello, World!\");\n" +
                        "    }\n" +
                        "}";
        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto =  codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    @Test
    public void testCompileAndRunCodeCompilationError() {

        String sourceCode =
                "public class Main {\n" +
                        "    public static void main(String[] args) {\n" +
                        "        System.out.println(\"Hello, World!\")\n" + //falta ;
                        "    }\n" +
                        "}";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto =  codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertFalse(resultDto.isCompile());
        //verificar que resultDto.getMeassage empieze por "Compilation failed: "
        Assertions.assertTrue(resultDto.getMessage().startsWith("Compilation failed: "));
    }

    @Test
    public void testCompileAndRunCodeExecutionError() {

        String sourceCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int num = 10;\n" +
                "        int div = 0;\n" +
                "        System.out.println(num / div);\n" +
                "    }\n" +
                "};";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto =  codeExecutionService.compileAndRunCode(sourceCode, codeResult);

        //verificar que resultDto tenga los valores esperados
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: "));
    }

    @Test
public void testCompileAndRunCodeNoMainClass() {

    String sourceCode = "public class NoMain {\n" +//main mal escrito
            "    public static void main(String[] args) {\n" +
            "        int num = 10;\n" +
            "        int div = 0;\n" +
            "        System.out.println(num / div);\n" +
            "    }\n" +
            "};";
    String codeResult = "Hello, World!";

    ExecutionResultDto resultDto =  codeExecutionService.compileAndRunCode(sourceCode, codeResult);

    //verificar que resultDto tenga los valores esperados
    Assertions.assertTrue(resultDto.isCompile());
    Assertions.assertFalse(resultDto.isExecution());
    Assertions.assertFalse(resultDto.isResultCodeMatch());
    Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: "));
    }
}
