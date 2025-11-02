package com.itachallenge.user.exception;

import com.itachallenge.errorcore.exception.ApiCustomErrorInfo;
import com.itachallenge.errorcore.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends BaseApiException {
    public UsernameAlreadyExistsException(String username) {
        super("The username '" + username + "' is already registered.", ApiCustomErrorInfo.of(HttpStatus.CONFLICT,"custom.username.already.exists",new Object[]{username}));
    }
}
