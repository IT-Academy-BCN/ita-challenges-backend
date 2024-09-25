package com.itachallenge.score.component;

import com.itachallenge.score.dto.ExecutionResultDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.janino.SimpleCompiler;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompilationResult {
    private ExecutionResultDto executionResultDto;
    private SimpleCompiler compiler;
}
