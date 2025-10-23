package com.itachallenge.errorcore.testapp;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.exceptionhandler.BaseExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TestExceptionHandler extends BaseExceptionHandler {
    public TestExceptionHandler(ErrorResponseBuilder responseBuilder) {
        super(responseBuilder);
    }
}

