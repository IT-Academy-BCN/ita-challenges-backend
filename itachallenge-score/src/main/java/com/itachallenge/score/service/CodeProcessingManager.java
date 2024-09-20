package com.itachallenge.score.service;

import com.itachallenge.score.document.ScoreRequest;
import com.itachallenge.score.document.ScoreResponse;
import com.itachallenge.score.dto.ExecutionResult;
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

        ExecutionResult executionResult = filterChain.apply(sourceCode);

        if (executionResult.isPassedAllFilters()) {
            executionResult = compileExecuter.executeCode(sourceCode);
        }

        ScoreResponse scoreResponse = new ScoreResponse();
        scoreResponse.setUuidChallenge(scoreRequest.getUuidChallenge());
        scoreResponse.setUuidLanguage(scoreRequest.getUuidLanguage());
        scoreResponse.setSolutionText(scoreRequest.getSolutionText());
        scoreResponse.setExpected_Result(resultExpected);
        int score = calculateScore(executionResult, resultExpected);
        scoreResponse.setCompilation_Message(executionResult.getMessage());
        scoreResponse.setScore(score);

        return ResponseEntity.ok(scoreResponse);
    }

    public int calculateScore(ExecutionResult executionResult, String resultExpected) {

        if (!executionResult.isCompiled()) {
            if (executionResult.getMessage().isEmpty()) {
                executionResult.setMessage("Compilation error: " + executionResult.getMessage());
            }
            return 0;
        }
        if (!executionResult.isExecution()) {
            executionResult.setMessage("Execution error: " + executionResult.getMessage());
            return 25;
        }
        if (executionResult.getMessage().equals(resultExpected)) {
            executionResult.setMessage("Code compiled and executed, and result match: " + executionResult.getMessage());
            return 100;
        }
        if (executionResult.getMessage().contains(resultExpected)) {
            executionResult.setMessage("Code compiled and executed, and result partially match: " + executionResult.getMessage());
            return 75;
        }
        executionResult.setMessage("Code compiled and executed, but result doesn't match: " + executionResult.getMessage());
        return 50;
    }

}