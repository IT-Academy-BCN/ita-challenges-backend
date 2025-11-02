package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseApiException {

    public InternalServerErrorException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.INTERNAL_SERVER_ERROR,"custom.internal.server.error",new Object[]{message}));
    }

}