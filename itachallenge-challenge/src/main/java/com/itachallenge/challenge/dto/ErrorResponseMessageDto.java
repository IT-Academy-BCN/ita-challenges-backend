package com.itachallenge.challenge.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseMessageDto {
    private String message;

    public ErrorResponseMessageDto(String message) {
        this.message = message;
    }

}
