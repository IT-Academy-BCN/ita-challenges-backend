package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSolutionScoreDtoTest {
    @Test
    void testLombokGeneratedMethods() {
        UserSolutionScoreDto dto = UserSolutionScoreDto.builder().build();

        assertThat(dto).isNotNull();
        assertThat(dto.toString()).isNotEmpty();
        assertThat(dto.hashCode()).isNotZero();
        assertThat(dto.equals(new UserSolutionScoreDto())).isTrue();
    }

}