package com.itachallenge.challenge.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class ErrorMessageDto {
    private final String message;
    private final Map<String, String> errors;

    public ErrorMessageDto(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }
}
