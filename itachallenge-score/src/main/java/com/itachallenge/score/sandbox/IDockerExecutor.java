package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;

import java.io.IOException;

@FunctionalInterface
public interface IDockerExecutor {

    ExecutionResult execute(String javaCode, String[] args) throws IOException, InterruptedException;

}
