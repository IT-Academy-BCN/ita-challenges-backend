package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

@Component
public class CodeExecutionService {

    private static final Logger log = LoggerFactory.getLogger(CodeExecutionService.class);

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
            executionResultDto.setMessage("Compilation failed: " + e.getMessage());
            log.error(e.getMessage());
            return executionResultDto;
        }

        // Ejecutar el código
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream old = System.out;
        System.setOut(printStream);

        try {
            compiler.getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) new String[]{});
        } catch (ClassNotFoundException e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: Class not found - " + e.getMessage());
            log.error(e.getMessage());
            return executionResultDto;
        } catch (NoSuchMethodException e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: No Main method - " + e.getMessage());
            log.error(e.getMessage());
            return executionResultDto;
        } catch (IllegalAccessException e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: Illegal access - " + e.getMessage());
            log.error(e.getMessage());
            return executionResultDto;
        } catch (InvocationTargetException e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: Invocation target exception - " + e.getMessage());
            log.error(e.getMessage());
            return executionResultDto;
        } catch (Throwable e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: " + e.getMessage());
            return executionResultDto;
        }

        System.out.flush();
        System.setOut(old);

        // Ejecución correcta
        executionResultDto.setExecution(true);

        // Comparar el resultado
        result = outputStream.toString();
        if (result.equals(codeResult)) {
            executionResultDto.setResultCodeMatch(true);
            executionResultDto.setMessage("Code executed successfully, result matches expected result. Execution result: " + result);
        } else {
            executionResultDto.setResultCodeMatch(false);
            executionResultDto.setMessage("Code executed successfully, result does not match expected result. Execution result: " + result);
        }

        return executionResultDto;
    }
}