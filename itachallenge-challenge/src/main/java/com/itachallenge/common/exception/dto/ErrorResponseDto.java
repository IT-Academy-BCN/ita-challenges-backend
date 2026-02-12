package com.itachallenge.common.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponseDto {
    private String errorCode;
    private String message;
    private String timestamp;
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> details;
}
