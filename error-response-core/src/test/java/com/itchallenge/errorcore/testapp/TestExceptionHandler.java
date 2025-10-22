package com.itchallenge.errorcore.testapp;

import com.itchallenge.errorcore.builder.ErrorResponseBuilder;
import com.itchallenge.errorcore.exceptionhandler.BaseExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TestExceptionHandler extends BaseExceptionHandler {
    public TestExceptionHandler(ErrorResponseBuilder responseBuilder) {
        super(responseBuilder);
    }
}

