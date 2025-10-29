package com.itachallenge.user.interactions.dto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageResponseTest {
    @Test
    void shouldInstantiatePageResponse() {
        PageResponse response = new PageResponse();
        assertNotNull(response);
    }
}