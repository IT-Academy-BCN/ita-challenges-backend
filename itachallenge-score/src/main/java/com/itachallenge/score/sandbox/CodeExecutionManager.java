package com.itachallenge.score.sandbox;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import com.itachallenge.score.sandbox.sandbox_filter.Filter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static java.util.logging.Logger.getLogger;

@Component
public class CodeExecutionManager {

    @Qualifier("createFilterChain") // Specify the bean to be injected
    private final Filter filterChain;

    private static final Logger log = getLogger(CodeExecutionManager.class.getName());
    private final JavaSandboxContainer javaSandboxContainer;

    public CodeExecutionManager(@Qualifier("compileExecuterFilter") Filter filterChain, JavaSandboxContainer javaSandboxContainer) {
        this.filterChain = filterChain;
        this.javaSandboxContainer = javaSandboxContainer;
    }

    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        String sourceCode = scoreRequest.getSolutionText();
        String resultExpected = "5432"; // TODO: Change to dynamic result from the challenge UUID

        ExecutionResultDto executionResultDto = filterChain.apply(sourceCode, resultExpected);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setCompilationMessage(executionResultDto.getMessage());

        if (!executionResultDto.isCompiled()) {
            scoreResponse.setScore(0);
            javaSandboxContainer.stopContainer();
            return ResponseEntity.badRequest().body(scoreResponse);
        }

        int score = calculateScore(executionResultDto, resultExpected);
        scoreResponse.setScore(score);

        javaSandboxContainer.stopContainer();
        return ResponseEntity.ok(scoreResponse);
    }

    public int calculateScore(String stdout, String stderr, String expectedOutput) {
        if (!stderr.isEmpty()) {
            log.info("Error de compilación o ejecución: {}" + stderr);
            // Error de compilación o de ejecución => Score 0
            return 0;
        }

        // Si stdout está vacío, significa que hubo un error de ejecución
        if (stdout.isEmpty()) {
            log.info("Ejecución fallida o salida vacía");
            // Score bajo por ejecución sin resultado
            return 50;
        }

        // Compara la salida estándar (stdout) con la salida esperada
        if (stdout.trim().equals(expectedOutput.trim())) {
            log.info("Salida correcta, asignando score máximo");
            return 100;
        } else {
            log.info("Salida incorrecta, evaluando score parcial");
            // Comparación aproximada para asignar un score parcial
            return 70;  // Por ejemplo, si la ejecución fue correcta pero la salida no es la esperada
        }
    }

}