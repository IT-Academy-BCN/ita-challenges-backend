package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.Assert.assertEquals;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DockerExecutorTest {

    @InjectMocks
    private DockerExecutor dockerExecutor;

    private GenericContainer<?> javaContainer;

    @BeforeEach
    public void setUp() {
        javaContainer = new GenericContainer<>(DockerImageName.parse("openjdk:11-slim"))
                .withCommand("sh", "-c", "while true; do sleep 1000; done"); // Infinite loop to keep the container running
        javaContainer.start();
        dockerExecutor.setWindowsCommand("cmd.exe");
        dockerExecutor.setUnixCommand("sh");
        dockerExecutor.setDockerImageName("openjdk:11-slim");
        dockerExecutor.setContainerName("java-executor-container");
        dockerExecutor.setCodeTemplate("import java.util.List;import java.util.ArrayList;public class Main { public static void main(String[] args) { %s } }");
        dockerExecutor.setTimeoutSeconds(5);
    }

    @ParameterizedTest
    @CsvSource({
            "42145, 54421",
            "12345, 54321",
            "98765, 98765"
    })
    void testRearrangeToGrow(int input, int expectedOutput) throws Exception {
        String sourceCode =
            "int input = Integer.parseInt(args[0]); " +
            "String inputString = String.valueOf(input); " +
            "char[] inputArray = inputString.toCharArray(); " +
            "for (int i = 0; i < inputArray.length; i++) { " +
            "    for (int j = i + 1; j < inputArray.length; j++) { " +
            "        if (inputArray[i] < inputArray[j]) { " +
            "            char temp = inputArray[i]; " +
            "            inputArray[i] = inputArray[j]; " +
            "            inputArray[j] = temp; " +
            "        } " +
            "    } " +
            "} " +
            "System.out.println(new String(inputArray));";


        String[] args = {String.valueOf(input)};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,3,4,5, 5, 1",
            "1,2,-3,4,5, 5, -3",
            "1,9,3,4,-5, 9, -5"
    })
    void testSearchingForExtremes(int a, int b, int c, int d, int e,  int expectedMax, int expectedMin) throws Exception {
        String sourceCode =
                "String[] inputs = args[0].split(\",\"); " +
                        "int a = Integer.parseInt(inputs[0]); " +
                        "int b = Integer.parseInt(inputs[1]); " +
                        "int c = Integer.parseInt(inputs[2]); " +
                        "int d = Integer.parseInt(inputs[3]); " +
                        "int e = Integer.parseInt(inputs[4]); " +
                        "int max = Math.max(Math.max(Math.max(Math.max(a, b), c), d), e); " +
                        "int min = Math.min(Math.min(Math.min(Math.min(a, b), c), d), e); " +

                        "System.out.println(max + \",\" + min);";

        String[] args = {String.format("%d,%d,%d,%d,%d", a, b, c, d, e)};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);

        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());

        String[] output = executionResult.getMessage().trim().split(",");
        assertEquals(String.valueOf(expectedMax), output[0].trim());
        assertEquals(String.valueOf(expectedMin), output[1].trim());
    }



    @ParameterizedTest
    @CsvSource({
            "3, false",
            "-1, false",
            "0, true"
    })
    void testYoureASquare(int input, boolean expectedOutput) throws Exception {

        String sourceCode =
                            "int input = Integer.parseInt(args[0]); " +
                            "boolean result = Math.sqrt(input) % 1 == 0;" +
                            "System.out.println(result);";
        String[] args = {String.valueOf(input)};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }

    @DisplayName("In this kata you will create a function that takes a list of non-negative integers and strings and returns a new list with the strings filtered out.")
    @ParameterizedTest
    @CsvSource({
            "1,2,a,b, '[1,2]'",
            "12341,542,pepe,asdf, '[12341,542]'",
            "5,6,e,f, '[5,6]'"
    })
    void testListFiltering(int a, int b, String c, String d, String expectedOutput) throws Exception {

                String sourceCode = "" +
                "int a = Integer.parseInt(args[0]); " +
                "int b = Integer.parseInt(args[1]); " +
                "String c = args[2]; " +
                "String d = args[3]; " +
                "List<Object> list = new ArrayList<>();" +
                "list.add(a);" +
                "list.add(b);" +
                "list.add(c);" +
                "list.add(d);" +
                "List<Object> result = new ArrayList<>();" +
                "for (Object o : list) {" +
                "    if (o instanceof Integer) {" +
                "        result.add(o);" +
                "    }" +
                "}" +
                "System.out.println(result);";

        String[] args = new String[]{String.valueOf(a), String.valueOf(b), c, d};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(expectedOutput.replace("'", "").trim(), executionResult.getMessage().trim().replace(" ", ""));
    }


    @ParameterizedTest
    @CsvSource({
            "Dermatoglyphics, true",
            "aba, false",
            "moOse, false"
    })
    void testIsograms(String input, boolean expectedOutput) throws Exception {

       String sourceCode = "String input = args[0]; " +
                                        "String result = input.toLowerCase();" +
                                        "boolean isIsogram = true;" +
                                        "for (int i = 0; i < result.length(); i++) {" +
                                            "for (int j = i + 1; j < result.length(); j++) {" +
                                                 "if (result.charAt(i) == result.charAt(j)) {" +
                                                    "isIsogram = false;" +
                                                "}" +
                                            "}" +
                                        "}" +
                                        "System.out.println(isIsogram);";
        String[] args = {input};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }
}
