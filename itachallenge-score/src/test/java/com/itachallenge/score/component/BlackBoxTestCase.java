package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BlackBoxTestCase {

    @Autowired
    CodeExecutionService codeExecutionService;

    //Test for bubble sort exercise
    String sourceCodeBubbleSort =
                    "        // Convertir los argumentos de cadena a enteros\n" +
                    "        int[] numbers = new int[args.length];\n" +
                    "            for (int i = 0; i < args.length; i++) {\n" +
                    "                numbers[i] = Integer.parseInt(args[i]);\n" +
                    "            }\n" +
                    "        // Aplicar el algoritmo de ordenación de burbuja\n" +
                    "        bubbleSort(numbers);\n" +
                    "        // Imprimir los números ordenados en una sola línea\n" +
                    "        for (int num : numbers) {\n" +
                    "            System.out.print(num + \" \");\n" +
                    "        }\n" +
                    "    }\n" +
                    "\n" +
                    "    // Implementación del algoritmo de ordenación de burbuja\n" +
                    "    private static void bubbleSort(int[] arr) {\n" +
                    "        int n = arr.length;\n" +
                    "        boolean swapped;\n" +
                    "        do {\n" +
                    "            swapped = false;\n" +
                    "            for (int i = 1; i < n; i++) {\n" +
                    "                if (arr[i - 1] > arr[i]) {\n" +
                    "                    // Intercambiar elementos si están en el orden incorrecto\n" +
                    "                    int temp = arr[i - 1];\n" +
                    "                    arr[i - 1] = arr[i];\n" +
                    "                    arr[i] = temp;\n" +
                    "                    swapped = true;\n" +
                    "                }\n" +
                    "            }\n" +
                    "        } while (swapped);\n";

    @Test
    public void bubbleSortTestCorrectParamInjection() {
        String sourceCode = sourceCodeBubbleSort;
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 2, 4, 1, 5, 3);

        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    public void bubbleSortTestIncorrectParamInjection() {
        String sourceCode = sourceCodeBubbleSort;
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 3, 5, "a", "bce", 5, 1);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: java.lang.NumberFormatException: "));
    }

    @Test
    public void bubbleSortTestIncorrectResult() {
        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 3, 5, 1, 2, 4);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    @Test
    public void infiniteLoopTest() {
        String sourceCode = "while(true) {}";
        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: Code execution timed out"));
    }


}
