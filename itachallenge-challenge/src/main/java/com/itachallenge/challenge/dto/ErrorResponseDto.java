package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@Getter
@Builder
public class ErrorResponseDto {
    private int status;
    String error;
    String message;
    private final List<FieldErrorDto> errors;
}
