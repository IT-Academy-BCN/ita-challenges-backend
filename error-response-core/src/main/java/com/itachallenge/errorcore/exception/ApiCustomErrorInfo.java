package com.itachallenge.errorcore.exception;

import org.springframework.http.HttpStatus;

public record ApiCustomErrorInfo(HttpStatus status, String messageKey, Object[] messageArgs ) {
    public static ApiCustomErrorInfo of(HttpStatus status, String messageKey, Object[] messageArgs){
        return new ApiCustomErrorInfo(status,messageKey,messageArgs);
    }
}
