package com.itachallenge.user.interactions.controller.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkControllerTest {
    @Test
    void shouldInstantiateBookmarkController() {
        BookmarkController controller = new BookmarkController();
        assertNotNull(controller);
    }

}