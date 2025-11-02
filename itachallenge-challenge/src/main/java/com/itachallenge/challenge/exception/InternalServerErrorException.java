package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends BaseApiException {
    public InternalServerErrorException(String message) {
        super(ApiCustomErrorInfo.of(HttpStatus.INTERNAL_SERVER_ERROR,"custom.internal.server.error", new Object[]{message}));
    }
    @Override
    public String getMessage(){
        return getInfo().messageArgs()[0].toString();
    }
}
