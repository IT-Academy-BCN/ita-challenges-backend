package com.itachallenge.score.sandbox;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import com.itachallenge.score.sandbox.sandbox_filter.Filter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CodeExecutionManager {

    @Qualifier("createFilterChain") // Specify the bean to be injected
    private final Filter filterChain;

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
        scoreResponse.setExpected_result(resultExpected);
        int score = calculateScore(executionResultDto, resultExpected);
        scoreResponse.setScore(score);

        javaSandboxContainer.stopContainer();
        return ResponseEntity.ok(scoreResponse);
    }

    private int calculateScore(ExecutionResultDto executionResultDto, String resultExpected) {

    if (!executionResultDto.isCompiled()) {
        return 0;
    }
    if (!executionResultDto.isExecution()) {
        return 25;
    }
    if (executionResultDto.getMessage().equals(resultExpected)) {
        return 100;
    }
    if (executionResultDto.getMessage().contains(resultExpected)) {
        return 75;
    }
    return 50;
}
}