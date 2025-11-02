package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseApiException {
    public BadRequestException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.BAD_REQUEST,"custom.bad.request",new Object[] {message}));
    }

}
