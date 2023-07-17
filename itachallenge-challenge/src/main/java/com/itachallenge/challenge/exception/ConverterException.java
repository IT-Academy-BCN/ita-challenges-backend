package com.itachallenge.challenge.exception;

public class ConverterException extends RuntimeException{

    public ConverterException(String message, Exception e) {
        super(message, e);
    }
}
