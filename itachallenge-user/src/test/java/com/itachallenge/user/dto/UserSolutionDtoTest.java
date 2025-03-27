package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserSolutionDtoTest {
    @Test
    void testLombokGeneratedMethods() {
        UserSolutionDto dto1 = UserSolutionDto.builder().build();
        UserSolutionDto dto2 = UserSolutionDto.builder().build();
        assertThat(dto1).isNotNull();
        assertThat(dto1.toString()).isNotEmpty();
        assertThat(dto1.hashCode()).isNotZero();
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.getClass()).isEqualTo(UserSolutionDto.class);
    }
}