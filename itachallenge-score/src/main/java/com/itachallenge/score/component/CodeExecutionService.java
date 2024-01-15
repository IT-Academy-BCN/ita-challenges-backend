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
import java.util.Arrays;

@Component
public class CodeExecutionService {

    /*RECEPCIÓN DEL CÓDIGO DEL USUARIO
    La cabecera -- public class Main{ public static void main(String[] args){ }}"; --
    ya viene por defecto, el usuario solo debe agregar el código que se le pide en el enunciado.

    La inyección de parámetros al código del cliente se hace mediante el uso varargs, el método castArgs,
    nos permite llamar al método pasando un array de objetos o pasando los objetos directamente, por ejemplo:
    Object[] args = new Object[]{"hola", 1, 2.0};
    compileAndRunCode(sourceCode, codeResult, args);
    compileAndRunCode(sourceCode, codeResult, "hola", 1, 2.0);
     */

    private static final Logger log = LoggerFactory.getLogger(CodeExecutionService.class);

    public ExecutionResultDto compileAndRunCode(String sourceCode, String codeResult, Object... args) {

        sourceCode = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                sourceCode +
                "    }\n" +
                "}";

        SimpleCompiler compiler = null;
        String result = null;

        //Compilar el código
        CompilationResult compilationResult = compile(sourceCode);
        ExecutionResultDto executionResultDto = compilationResult.getExecutionResultDto();

        //Descomposición y tipado de parámetros de entrada
        String[] argsString = castArgs(args);

        if (executionResultDto.isCompile()) {
            //Ejecutar el código
            ExecutionResult executionResult = execute(compilationResult, codeResult, argsString);
            if (executionResultDto.isExecution()) {
                //Comparar el resultado
                executionResultDto = compareResults(executionResult.getExecutionResult(), codeResult, executionResultDto);
            }
        }
        return executionResultDto;
    }

    public CompilationResult compile(String sourceCode) {
        ExecutionResultDto executionResultDto = new ExecutionResultDto(false, false, false, "");
        SimpleCompiler compiler = null;

        try {
            compiler = new SimpleCompiler();
            compiler.cook(sourceCode);
            // Si la compilación es exitosa, actualizar ExecutionResultDto
            executionResultDto.setCompile(true);
        } catch (CompileException e) {
            // Si la compilación falla, actualizar y devolver ExecutionResultDto
            executionResultDto.setMessage("Compilation failed: " + e.getMessage());
            log.error(e.getMessage());
            return new CompilationResult(executionResultDto, null);
        }

        return new CompilationResult(executionResultDto, compiler);
    }

    public ExecutionResult execute(CompilationResult compilationResult, String codeResult, String[] args) {
        ExecutionResult executionResult = new ExecutionResult(compilationResult.getExecutionResultDto(), "");
        ExecutionResultDto executionResultDto = compilationResult.getExecutionResultDto();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        PrintStream old = System.out;
        System.setOut(printStream);

        try {
            compilationResult.getCompiler().getClassLoader().loadClass("Main")
                    .getMethod("main", String[].class)
                    .invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            log.error(e.getMessage());
        } catch (InvocationTargetException e) {
            log.error(e.getMessage() + " " + e.getTargetException());
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: " + e.getTargetException());
            return executionResult;
        } catch (Throwable e) {
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Execution failed: " + e.getMessage());
            return executionResult;
        }

        System.out.flush();
        System.setOut(old);

        // Ejecución correcta
        executionResultDto.setExecution(true);
        executionResult.setExecutionResult(outputStream.toString());

        return executionResult;
    }

    public ExecutionResultDto compareResults(String result, String codeResult, ExecutionResultDto executionResultDto) {
        if (result.equals(codeResult)) {
            executionResultDto.setResultCodeMatch(true);
            executionResultDto.setMessage("Code executed successfully, result matches expected result. Execution result: " + result);
        } else {
            executionResultDto.setResultCodeMatch(false);
            executionResultDto.setMessage("Code executed successfully, result does not match expected result. Execution result: " + result);
        }

        return executionResultDto;
    }

    public String[] castArgs(Object... args) {
        // Comprobar si el primer elemento es un array
        if (args.length == 1 && args[0] instanceof Object[] innerArgs) {
            // Si es un array, tratarlo como tal
            String[] argsString = new String[innerArgs.length];
            for (int i = 0; i < innerArgs.length; i++) {
                argsString[i] = innerArgs[i].toString();
            }
            return argsString;
        } else {
            // Si no es un array, tratarlo como antes
            String[] argsString = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                argsString[i] = args[i].toString();
            }
            return argsString;
        }
    }


}