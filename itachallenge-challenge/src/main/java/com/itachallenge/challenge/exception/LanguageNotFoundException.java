package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class LanguageNotFoundException extends BaseApiException {
    public LanguageNotFoundException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"custom.language.not.found", new Object[]{message}));
    }
}
