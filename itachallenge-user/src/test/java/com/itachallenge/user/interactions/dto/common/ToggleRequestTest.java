package com.itachallenge.user.interactions.dto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToggleRequestTest {
    @Test
    void shouldInstantiateToggleRequest() {
        ToggleRequest request = new ToggleRequest();
        assertNotNull(request);
    }
}