package com.itachallenge.score.service;

import com.itachallenge.score.dto.ScoreRequest;
import com.itachallenge.score.dto.ScoreResponse;
import com.itachallenge.score.exception.DockerExecutionException;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.sandbox.DockerExecutor;
import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.io.File;

import java.io.IOException;

@Component
public class CodeProcessingManager {

    private static final Logger log = LoggerFactory.getLogger(CodeProcessingManager.class);

    private final Filter filterChain;
    private final DockerExecutor dockerExecutor;
    private final JavaFileService javaFileService; // Inyección del servicio para crear archivos Java

    @Autowired
    public CodeProcessingManager(Filter filterChain, DockerExecutor dockerExecutor, JavaFileService javaFileService) {
        this.filterChain = filterChain;
        this.dockerExecutor = dockerExecutor;
        this.javaFileService = javaFileService; // Inyectamos el servicio
    }

    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        String sourceCode = scoreRequest.getSolutionText(); // El código del usuario
        String[] arguments = {"5", "7"}; // Parámetros de entrada del reto
        String resultExpected = "12"; // Resultado esperado del reto

        // Aplicamos los filtros al código del usuario
        ExecutionResult executionResult = filterChain.apply(sourceCode);

        // Si no pasa los filtros, devolvemos un puntaje de 0
        if (!executionResult.isSuccess()) {
            ScoreResponse scoreResponse = new ScoreResponse();
            scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
            scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
            scoreResponse.setSolutionText(scoreRequest.getSolutionText());
            scoreResponse.setExpectedResult(resultExpected);
            scoreResponse.setCompilationMessage(executionResult.getMessage().trim());
            scoreResponse.setScore(0);
            return ResponseEntity.ok(scoreResponse);
        }

        // Si el código pasa los filtros, generamos el archivo Java
        File javaFile;
        try {
            // Ruta en el host, mapeada a la ruta en el contenedor Docker
            String filePath = "/tmp/java-files/UserSolution.java"; // Ajusta según la configuración de tu Docker
            javaFile = javaFileService.createJavaFile(sourceCode, filePath);
        } catch (IOException e) {
            // Si hay un error al generar el archivo, retornamos 0 y el mensaje de error
            ScoreResponse scoreResponse = new ScoreResponse();
            scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
            scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
            scoreResponse.setSolutionText(scoreRequest.getSolutionText());
            scoreResponse.setExpectedResult(resultExpected);
            scoreResponse.setCompilationMessage("Error al crear el archivo Java: " + e.getMessage());
            scoreResponse.setScore(0);
            return ResponseEntity.ok(scoreResponse);
        }

        // Ejecutamos el código en el contenedor Docker
        try {
            // Ejecutamos el archivo Java en Docker con los parámetros proporcionados
            executionResult = dockerExecutor.execute(javaFile.getAbsolutePath(), arguments); // Ejecutamos el archivo generado
        } catch (IOException e) {
            ScoreResponse scoreResponse = new ScoreResponse();
            scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
            scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
            scoreResponse.setSolutionText(scoreRequest.getSolutionText());
            scoreResponse.setExpectedResult(resultExpected);
            scoreResponse.setCompilationMessage("Execution timed out: " + e.getMessage());
            scoreResponse.setScore(0);
            return ResponseEntity.ok(scoreResponse);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DockerExecutionException("Execution interrupted", e);
        }

        // Generamos la respuesta con el puntaje
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setExpectedResult(resultExpected);
        int score = calculateScore(executionResult, resultExpected);
        scoreResponse.setCompilationMessage(executionResult.getMessage().trim());
        scoreResponse.setScore(score);

        // Logueamos el resultado
        if (executionResult.getMessage().contains("TIMED OUT")) {
            log.info(scoreResponse.getCompilationMessage());
        } else {
            log.info("Code processed successfully: {}", scoreResponse.getCompilationMessage());
        }

        return ResponseEntity.ok(scoreResponse);
    }


    public int calculateScore(ExecutionResult executionResult, String resultExpected) {
        String trimmedMessage = executionResult.getMessage().trim();

        if (!executionResult.isCompiled()) {
            if (trimmedMessage.isEmpty()) {
                executionResult.setMessage("Compilation error: " + trimmedMessage);
            }
            return 0;
        }
        if (!executionResult.isExecution()) {
            executionResult.setMessage("Execution error: " + trimmedMessage);
            return 25;
        }
        if (trimmedMessage.equals(resultExpected)) {
            executionResult.setMessage("Code compiled and executed, and result match: " + trimmedMessage);
            return 100;
        }
        if (trimmedMessage.contains(resultExpected)) {
            executionResult.setMessage("Code compiled and executed, and result partially match: " + trimmedMessage);
            return 75;
        }
        executionResult.setMessage("Code compiled and executed, but result doesn't match: " + trimmedMessage);
        return 50;
    }
}