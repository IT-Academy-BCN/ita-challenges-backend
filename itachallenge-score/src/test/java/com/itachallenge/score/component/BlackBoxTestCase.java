package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlackBoxTestCase {

    @Autowired
    CodeExecutionService codeExecutionService;

    //Test for bubble sort exercise
    String sourceCodeBubbleSort = """
        // Convertir los argumentos de cadena a enteros
        int[] numbers = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            numbers[i] = Integer.parseInt(args[i]);
        }
        // Aplicar el algoritmo de ordenación de burbuja
        bubbleSort(numbers);
        // Imprimir los números ordenados en una sola línea
        for (int num : numbers) {
            System.out.print(num + " ");
        }
    }

    // Implementación del algoritmo de ordenación de burbuja
    private static void bubbleSort(int[] arr) {
        int n = arr.length;
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < n; i++) {
                if (arr[i - 1] > arr[i]) {
                    // Intercambiar elementos si están en el orden incorrecto
                    int temp = arr[i - 1];
                    arr[i - 1] = arr[i];
                    arr[i] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }
    """;

    @Test
    void bubbleSortTestCorrectParamInjection() {
        String sourceCode = sourceCodeBubbleSort;
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 2, 4, 1, 5, 3);

        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }

    @Test
    void bubbleSortTestIncorrectParamInjection() {
        String sourceCode = sourceCodeBubbleSort;
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 3, 5, "a", "bce", 5, 1);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: java.lang.NumberFormatException: "));
    }

    @Test
    void bubbleSortTestIncorrectResult() {
        String sourceCode = "System.out.println(\"Hello, World!\");\n";
        String codeResult = "1 2 3 4 5 ";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 3, 5, 1, 2, 4);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertFalse(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result does not match expected result. Execution result: "));
    }

    @Test
    void infiniteLoopTest() {
        String sourceCode = "while(true) {}";
        String codeResult = "Hello, World!\n";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertFalse(resultDto.isExecution());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Execution failed: Code execution timed out"));
    }

    String sortingCode = """
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


    @Test
    void sortingCodeTest() {
        String sourceCode = sortingCode;
        String codeResult = "1234";

        ExecutionResultDto resultDto = codeExecutionService.compileAndRunCode(sourceCode, codeResult, 4321);
        Assertions.assertTrue(resultDto.isCompile());
        Assertions.assertTrue(resultDto.isExecution());
        Assertions.assertTrue(resultDto.isResultCodeMatch());
        Assertions.assertTrue(resultDto.getMessage().startsWith("Code executed successfully, result matches expected result. Execution result: "));
    }


}
