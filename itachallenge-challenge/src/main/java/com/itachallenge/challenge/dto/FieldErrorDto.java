package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@Getter
public class FieldErrorDto {
    private final String objectName;
    private final String field;
    private final String message;
}

