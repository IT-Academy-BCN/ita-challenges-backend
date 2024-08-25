package com.itachallenge.score.docker;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

public class CodeExecutionManager {

    @Autowired
    private Filter filterChain;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Autowired
    private CodeExecutionService codeExecutionService;

    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        String sourceCode = scoreRequest.getSolutionText();

        boolean isValid = filterChain.apply(sourceCode);

        if (!isValid) {
            javaSandboxContainer.stopContainer();
            ScoreResponse errorResponse = new ScoreResponse();
            errorResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
            errorResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
            errorResponse.setSolutionText(scoreRequest.getSolutionText());
            errorResponse.setScore(0);
            errorResponse.setCompilationMessage("Code is not valid");

            return ResponseEntity.badRequest().body(errorResponse);
        }

        int score = codeExecutionService.calculateScore(new ExecutionResultDto());

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setScore(score);
        scoreResponse.setCompilationMessage("Code executed successfully");
        javaSandboxContainer.stopContainer();

        return ResponseEntity.ok(scoreResponse);
    }

}
