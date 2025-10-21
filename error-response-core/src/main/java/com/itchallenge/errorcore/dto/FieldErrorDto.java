package com.itchallenge.errorcore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@AllArgsConstructor
@Getter
public class FieldErrorDto {
    private final String objectName;
    private final String field;
    private final String message;
}
