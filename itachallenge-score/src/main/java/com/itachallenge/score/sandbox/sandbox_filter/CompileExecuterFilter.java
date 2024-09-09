package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.dto.ExecutionResultDto;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

@Getter
@Setter
@Component
public class CompileExecuterFilter implements Filter {

    private static final Logger log = getLogger(CompileExecuterFilter.class.getName());

    private Filter next;

    private CodeExecutionService codeExecutionService;

    @Autowired
    public CompileExecuterFilter(CodeExecutionService codeExecutionService) {
        this.codeExecutionService = codeExecutionService;
    }

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {
        if (codeExecutionService == null) {
            throw new IllegalStateException("CodeExecutionService is not initialized");
        }

        try {
            return codeExecutionService.compileAndRunCode(code, resultExpected, new Object[]{});
        } catch (Exception e) {
            log.error("Error compiling and executing code", e);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();
            executionResultDto.setCompiled(false);
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Error compiling and executing code: " + e.getMessage());
            return executionResultDto;
        }
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}