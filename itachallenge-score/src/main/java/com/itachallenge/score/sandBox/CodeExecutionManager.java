package com.itachallenge.score.sandBox;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandBox.sandBoxContainer.JavaSandboxContainer;
import com.itachallenge.score.sandBox.sandBox_filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CodeExecutionManager {

    @Autowired
    @Qualifier("createFilterChain") // Specify the bean to be injected
    private Filter filterChain;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Autowired
    private CodeExecutionService codeExecutionService;

    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        String sourceCode = scoreRequest.getSolutionText();

        String resultExpected = "99";

        //TODO: We need to change the expected result to be dynamic result from the challenge UUID;

        ExecutionResultDto executionResultDto = filterChain.apply(sourceCode, resultExpected);

        if (!executionResultDto.isCompiled()) {
            javaSandboxContainer.stopContainer();
            ScoreResponse errorResponse = new ScoreResponse();
            errorResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
            errorResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
            errorResponse.setSolutionText(scoreRequest.getSolutionText());
            errorResponse.setScore(0);
            errorResponse.setCompilationMessage(executionResultDto.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }


        int score = codeExecutionService.calculateScore(executionResultDto);

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setScore(score);
        scoreResponse.setCompilationMessage(executionResultDto.getMessage());
        javaSandboxContainer.stopContainer();

        return ResponseEntity.ok(scoreResponse);
    }

}
