package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
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
class ChallengeTests {

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
                            "String result = new StringBuilder(String.valueOf(input)).reverse().toString();" +
                            "System.out.println(result);";

        String[] args = {String.valueOf(input)};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,3,4,5, 5",
            "10,20,30,40,50, 50",
            "-1,-2,-3,-4,-5, -1"
    })
    void testSearchingForExtremes(String input, int expectedOutput) throws Exception {
        String sourceCode = "String[] inputs = args[0].split(\",\"); " +
                            "int max = Integer.MIN_VALUE; " +
                            "for (String s : inputs) { max = Math.max(max, Integer.parseInt(s)); }" +
                            "System.out.println(max);";
        String[] args = {input};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }

    @ParameterizedTest
    @CsvSource({
            "16, true",
            "25, true",
            "26, false"
    })
    void testYoureASquare(int input, boolean expectedOutput) throws Exception {
        String sourceCode = "int input = Integer.parseInt(args[0]); " +
                            "boolean result = Math.sqrt(input) % 1 == 0;" +
                            "System.out.println(result);";
        String[] args = {String.valueOf(input)};
        ExecutionResult executionResult = dockerExecutor.execute(sourceCode, args);
        assertEquals(true, executionResult.isCompiled());
        assertEquals(true, executionResult.isExecution());
        assertEquals(String.valueOf(expectedOutput), executionResult.getMessage().trim());
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,a,b, '[1,2]'",
            "3,4,c,d, '[3,4]'",
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
