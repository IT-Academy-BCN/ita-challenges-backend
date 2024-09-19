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

    private final CompileExecuter compileExecuter;

    @Autowired
    public CodeProcessingManager(@Qualifier("createFilterChain") Filter filterChain, CompileExecuter compileExecuter) {
        this.filterChain = filterChain;
        this.compileExecuter = compileExecuter;
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
        scoreResponse.setExpected_Result(resultExpected);
        int score = calculateScore(executionResultDto, resultExpected);
        scoreResponse.setCompilation_Message(executionResultDto.getMessage());
        scoreResponse.setScore(score);

        return ResponseEntity.ok(scoreResponse);
    }

    public int calculateScore(ExecutionResultDto executionResultDto, String resultExpected) {
        if (!executionResultDto.isCompiled()) {
            if (executionResultDto.getMessage().isEmpty()) {
                executionResultDto.setMessage("Compilation error: " + executionResultDto.getMessage());
            }
            return 0;
        }
        if (!executionResultDto.isExecution()) {
            executionResultDto.setMessage("Execution error: " + executionResultDto.getMessage());
            return 25;
        }
        if (executionResultDto.getMessage().equals(resultExpected)) {
            executionResultDto.setMessage("Code compiled and executed, and result match: " + executionResultDto.getMessage());
            return 100;
        }
        if (executionResultDto.getMessage().contains(resultExpected)) {
            executionResultDto.setMessage("Code compiled and executed, and result partially match: " + executionResultDto.getMessage());
            return 75;
        }
        executionResultDto.setMessage("Code compiled and executed, but result doesn't match: " + executionResultDto.getMessage());
        return 50;
    }

}