package com.itachallenge.challenge.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class MessageDto {
    private String message;

    public MessageDto(String message) {
        this.message = message;
    }

}