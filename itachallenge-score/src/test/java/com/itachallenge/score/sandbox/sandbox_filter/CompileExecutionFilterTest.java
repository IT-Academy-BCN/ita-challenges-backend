package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.CompileExecuter;
import com.itachallenge.score.service.CodeProcessingManager;
import com.itachallenge.score.sandbox.JavaSandboxContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CompileExecutionFilterTest {

    @Autowired
    private CompileExecuter compileExecuter;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Autowired
    private CodeProcessingManager codeProcessingManager;

    @Test
    void testCompileAndRunCode() {
        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeResultNotMatch() {
        String sourceCode = "System.out.println(\"Bad Hello, World!\");\n";
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.getMessage().startsWith("Hello, World!"));
    }

    @Test
    void testCompileAndRunCodeCompilationError() {
        String sourceCode = "System.out.println(\"Hello, World!\")\n";  // Missing semicolon
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        Assertions.assertFalse(resultDto.isCompiled());
        assertTrue(resultDto.getMessage().startsWith("Compiled error:"));
    }

    @Test
    void testCompileAndRunCodeExecutionError() {
        String sourceCode = """
                int num = 10;
                int div = 0;
                System.out.println(num / div);
                """;
        String codeResult = "Hello, World!";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("Execution error:"));
    }

    @Test
    void testCompileAndRunCode2() {
        String sourceCode = """
                int num = 10;
                System.out.println(num / 2);
                """;
        String codeResult = "5";  // Expected result

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertTrue(resultDto.getMessage().equals(codeResult));
    }

    @Test
    void testCompileAndRunCodeWithWrongTypeParameterInjection() {
        String sourceCode = """
                int num = Integer.parseInt(args[0]);
                System.out.println(num / 2);
                """;
        String codeResult = "5";  // Expected result

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
    }

    @Test
    void bubbleSortTestCorrectParamInjection() {
        // El código fuente que se inyectará en el método main
        String sourceCode = """
        int[] numbers = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            numbers[i] = Integer.parseInt(args[i]);
        }
        bubbleSort(numbers);
        for (int num : numbers) {
            System.out.print(num + " ");
        }

        // Método bubbleSort, no necesita ser estático en este caso
        public static void bubbleSort(int[] arr) {
            int n = arr.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
        }
    """;

        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        System.out.println(resultDto.getMessage());
        assertTrue(resultDto.isCompiled(), "Código no compilado correctamente.");
        assertTrue(resultDto.isExecution(), "Código no ejecutado correctamente.");
        assertTrue(resultDto.getMessage().startsWith(codeResult), "El resultado de la ejecución no es el esperado.");
    }



    @Test
    void bubbleSortTestIncorrectParamInjection() {
        String sourceCode = """
                int[] numbers = new int[args.length];
                for (int i = 0; i < args.length; i++) {
                    numbers[i] = Integer.parseInt(args[i]);
                }
                bubbleSort(numbers);
                for (int num : numbers) {
                    System.out.print(num + " ");
                }
                """;
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
    }

    @Test
    void bubbleSortTestIncorrectResult() {
        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.getMessage().startsWith("1 2 3 4 5 "));
    }

    @Test
    void infiniteLoopTest() {
        String sourceCode = "while(true) {}";
        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);
        assertTrue(resultDto.isCompiled());
        Assertions.assertFalse(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("Execution failed: Code execution timed out"));
    }

    @Test
    void sortingCodeTest() {
        String sourceCode = """
                int number = Integer.parseInt(args[0]);
                String numberStr = Integer.toString(number);
                List<Character> chars = new ArrayList<>();
                for (char c : numberStr.toCharArray()) {
                    chars.add(c);
                }
                Collections.sort(chars);
                StringBuilder sortedNumberStr = new StringBuilder(chars.size());
                for (Character c : chars) {
                    sortedNumberStr.append(c);
                }
                System.out.println(sortedNumberStr.toString());
                """;
        String codeResult = "1234";

        ExecutionResultDto resultDto = compileExecuter.executeCode(sourceCode, codeResult);

        assertTrue(resultDto.isCompiled());
        assertTrue(resultDto.isExecution());
        assertTrue(resultDto.getMessage().startsWith("1234"));
    }
}