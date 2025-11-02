package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class DatabaseException extends BaseApiException {
    public DatabaseException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.INTERNAL_SERVER_ERROR,"custom.database",new Object[]{message}));
    }
}
