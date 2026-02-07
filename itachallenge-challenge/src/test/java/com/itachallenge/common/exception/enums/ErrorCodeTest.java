package com.itachallenge.common.exception.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void testEnumValues() {
        assertThat(ErrorCode.VALIDATION_ERROR.getCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(ErrorCode.CHALLENGE_NOT_FOUND.getCode()).isEqualTo("CHALLENGE_NOT_FOUND");
    }

    @Test
    void testEnumValueOf() {
        assertThat(ErrorCode.valueOf("VALIDATION_ERROR")).isEqualTo(ErrorCode.VALIDATION_ERROR);
    }

    @Test
    void testValuesArray() {
        assertThat(ErrorCode.values()).containsExactlyInAnyOrder(
                ErrorCode.VALIDATION_ERROR,
                ErrorCode.CHALLENGE_NOT_FOUND
        );
    }
}
