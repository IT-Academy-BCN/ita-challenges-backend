package com.itachallenge.user.exceptions;

public class BadUUIDException extends IllegalArgumentException {
    public BadUUIDException(String msg){
        super(msg);
    }
    public BadUUIDException(){}
}
