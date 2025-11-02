package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class TagNotFoundException extends BaseApiException {
    public TagNotFoundException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"custom.tag.not.found", new Object[]{message}));
    }
}
