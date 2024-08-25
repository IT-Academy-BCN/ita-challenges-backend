package com.itachallenge.score.filter;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.dto.ExecutionResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompileExecuterFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CompileExecuterFilter.class);
    private Filter next;
    private CodeExecutionService codeExecutionService;

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {

        log.info("Compiling and executing code");

        ExecutionResultDto executionResultDto;
        try {
            executionResultDto = codeExecutionService.compileAndRunCode(code, resultExpected);
        } catch (Exception e) {
            log.error("Error compiling and executing code", e);
            executionResultDto = new ExecutionResultDto();
            executionResultDto.setCompile(false);
            executionResultDto.setExecution(false);
            executionResultDto.setMessage(e.getMessage());
            return executionResultDto;
        }


        return executionResultDto;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
