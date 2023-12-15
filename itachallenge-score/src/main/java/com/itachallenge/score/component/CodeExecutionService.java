package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

@Component
public class CodeExecutionService {

    public ExecutionResultDto compileAndRunCode(String sourceCode, String codeResult) {
        ExecutionResultDto executionResultDto = new ExecutionResultDto(false, false, false, "");
        SimpleCompiler compiler = null;
        String result = null;

        // Compilar el código
        try {
            compiler = new SimpleCompiler();
            compiler.cook(sourceCode);
            // Si la compilación es exitosa, actualizar ExecutionResultDto
            executionResultDto.setCompile(true);
        } catch (CompileException e) {
            // Si la compilación falla, actualizar y devolver ExecutionResultDto
            executionResultDto.setCompile(false);
            executionResultDto.setMessage("Compilation failed: " + e.getMessage());
            return executionResultDto;
        }

        // Ejecutar el código
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            PrintStream old = System.out;
            System.setOut(printStream);

            Class<?> compiledClass = compiler.getClassLoader().loadClass("Main");
            compiledClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});

            System.out.flush();
            System.setOut(old);

            // Ejecucion correctoa
            executionResultDto.setExecution(true);

            // Comparar el resultado
            result = outputStream.toString();
            if (result.equals(codeResult)) {
                executionResultDto.setResultCodeMatch(true);
                executionResultDto.setMessage("Code executed successfully, result matches expected result. Execution result: " + result);
            } else {
                executionResultDto.setResultCodeMatch(false);
                executionResultDto.setMessage("Code executed successfully, result does not match expected result. Execution result: " + result);            }

        } catch (Exception e) {
            // Si la ejecución falla, actualizar y devolver ExecutionResultDto
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: " + e.getCause());
            return executionResultDto;
        }

        return executionResultDto;
    }
}