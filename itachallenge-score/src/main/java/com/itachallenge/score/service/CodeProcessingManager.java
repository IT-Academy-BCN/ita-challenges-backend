package com.itachallenge.score.service;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.sandbox.CompileExecuter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class CodeProcessingManager {

    @Qualifier("createFilterChain") // Specify the bean to be injected
    private final Filter filterChain;

    @Autowired
    CompileExecuter compileExecuter;


    public CodeProcessingManager(@Qualifier("createFilterChain") Filter filterChain) {
        this.filterChain = filterChain;
    }

    public ResponseEntity<ScoreResponse> processCode(ScoreRequest scoreRequest) {

        String sourceCode = scoreRequest.getSolutionText();
        String resultExpected = "5432"; // TODO: Change to dynamic result from the challenge UUID

        ExecutionResultDto executionResultDto = filterChain.apply(sourceCode);

        if (executionResultDto.isPassedAllFilters()) {
            executionResultDto = compileExecuter.executeCode(sourceCode);
        }
        
        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setCompilationMessage(executionResultDto.getMessage());
        scoreResponse.setExpected_result(resultExpected);
        int score = calculateScore(executionResultDto, resultExpected);
        scoreResponse.setScore(score);

        return ResponseEntity.ok(scoreResponse);

    }

    public int calculateScore(ExecutionResultDto executionResultDto, String resultExpected) {

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