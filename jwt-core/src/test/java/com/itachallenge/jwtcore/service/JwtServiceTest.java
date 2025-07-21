package com.itachallenge.jwtcore.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void canInstantiateJwtService() {
        IJwtService jwtService = new JwtService();
        assertThat(jwtService).isNotNull();
    }
}
