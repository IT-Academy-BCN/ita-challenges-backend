package com.itachallenge.user.interactions.dto.bookmark;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkDtoTest {
    @Test
    void shouldInstantiateBookmarkDto() {
        BookmarkDto dto = new BookmarkDto();
        assertNotNull(dto);
    }
}