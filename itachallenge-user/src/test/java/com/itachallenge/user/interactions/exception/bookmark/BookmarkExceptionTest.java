package com.itachallenge.user.interactions.exception.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkExceptionTest {
    @Test
    void shouldInstantiateBookmarkException() {
        BookmarkException exception = new BookmarkException();
        assertNotNull(exception);
    }
}