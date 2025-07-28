package com.itachallenge.challenge.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class BookmarkDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        BookmarkDto dto = new BookmarkDto(true, 42);

        assertTrue(dto.isBookmarked());
        assertEquals(42, dto.getTimesBookmarked());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        BookmarkDto dto = new BookmarkDto();
        dto.setBookmarked(false);
        dto.setTimesBookmarked(10);

        assertFalse(dto.isBookmarked());
        assertEquals(10, dto.getTimesBookmarked());
    }

    @Test
    void testEqualsAndHashCode() {
        BookmarkDto dto1 = new BookmarkDto(true, 5);
        BookmarkDto dto2 = new BookmarkDto(true, 5);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testNoArgsConstructorDefaultValues() {
        BookmarkDto dto = new BookmarkDto();

        assertFalse(dto.isBookmarked());
        assertEquals(0, dto.getTimesBookmarked());
    }

    @Test
    void testToString() {
        BookmarkDto dto = new BookmarkDto(true, 100);
        String result = dto.toString();

        assertTrue(result.contains("BookmarkDto"));
        assertTrue(result.contains("isBookmarked=true"));
        assertTrue(result.contains("timesBookmarked=100"));
    }
}
