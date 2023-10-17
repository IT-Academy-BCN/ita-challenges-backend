package com.itachallenge.challenge.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseMessageDto {
    private int statusCode;
    private String message;

    public ErrorResponseMessageDto(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
