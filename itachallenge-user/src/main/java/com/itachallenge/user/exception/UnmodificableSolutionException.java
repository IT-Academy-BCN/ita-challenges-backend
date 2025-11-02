package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UnmodificableSolutionException extends BaseApiException {
    public UnmodificableSolutionException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.CONFLICT,"custom.bad.request",new Object[]{message}));
    }
}
