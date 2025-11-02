package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseApiException {

    public NotFoundException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"custom.not.found",new Object[]{message}));
    }

}
