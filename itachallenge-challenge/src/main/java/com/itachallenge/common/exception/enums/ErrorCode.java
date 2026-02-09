package com.itachallenge.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR,
    CHALLENGE_NOT_FOUND;
}
