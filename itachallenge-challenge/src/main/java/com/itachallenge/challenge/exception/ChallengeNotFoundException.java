package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class ChallengeNotFoundException extends BaseApiException {
    public ChallengeNotFoundException(String message) {
        super(message, ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"custom.challenge.not.found", new Object[]{message}));
    }
}
