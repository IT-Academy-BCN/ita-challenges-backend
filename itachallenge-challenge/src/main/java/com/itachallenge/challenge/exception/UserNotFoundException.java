package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseApiException {

    public UserNotFoundException(String message) {
        super(ApiCustomErrorInfo.of(HttpStatus.NOT_FOUND,"custom.user.not.found", new Object[]{message}));
    }
    @Override
    public String getMessage(){
        return getInfo().messageArgs()[0].toString();
    }
}
