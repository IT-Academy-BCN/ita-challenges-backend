package com.itachallenge.challenge.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ErrorMessageDto {
    private String message;
    private Map<String, String> errors = new HashMap<>();

    public ErrorMessageDto(String message) {
        this.message = message;
    }

    public ErrorMessageDto(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
