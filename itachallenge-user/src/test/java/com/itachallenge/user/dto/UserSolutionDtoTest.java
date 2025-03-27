package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSolutionDtoTest {
    @Test
    void testLombokGeneratedMethods() {
        UserSolutionDto dto = UserSolutionDto.builder().build();

        assertThat(dto).isNotNull();
        assertThat(dto.toString()).isNotEmpty();
        assertThat(dto.hashCode()).isNotZero();
        assertThat(dto.equals(new UserSolutionDto())).isTrue();
    }
}