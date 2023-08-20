package com.itachallenge.challenge.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorMessage {
    private String message;
    private Map<String, String> errors = new HashMap<>();

    public ErrorMessage(String message) {
        this.message = message;
    }

    public ErrorMessage(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
