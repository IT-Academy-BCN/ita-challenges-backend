package com.itachallenge.user.interactions.document.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkTest {
    @Test
    void shouldInstantiateBookmark() {
        Bookmark model = new Bookmark();
        assertNotNull(model);
    }

}