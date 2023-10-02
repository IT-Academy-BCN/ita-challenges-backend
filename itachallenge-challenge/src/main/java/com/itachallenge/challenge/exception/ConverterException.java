package com.itachallenge.challenge.exception;

public class ConverterException extends RuntimeException{

    public ConverterException() {
        super("Converter error");
    }

    public ConverterException(String message) {
        super(message);
    }
}
