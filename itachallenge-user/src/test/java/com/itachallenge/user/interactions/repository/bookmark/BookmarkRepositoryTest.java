package com.itachallenge.user.interactions.repository.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkRepositoryTest {
    @Test
    void shouldInstantiateBookmarkRepository() {
        BookmarkRepository repository = new BookmarkRepository();
        assertNotNull(repository);
    }
}