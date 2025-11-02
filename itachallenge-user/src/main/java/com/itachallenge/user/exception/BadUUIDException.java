package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class BadUUIDException extends BaseApiException {

    public BadUUIDException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.BAD_REQUEST,"custom.bad.uuid",new Object[]{message}));
    }
}
