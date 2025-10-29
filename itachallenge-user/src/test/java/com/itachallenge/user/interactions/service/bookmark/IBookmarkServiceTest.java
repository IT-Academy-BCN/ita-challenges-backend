package com.itachallenge.user.interactions.service.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IBookmarkServiceTest {
    @Test
    void shouldInstantiateIBookmarkService() {
        IBookmarkService service = new IBookmarkService();
        assertNotNull(service);
    }
}