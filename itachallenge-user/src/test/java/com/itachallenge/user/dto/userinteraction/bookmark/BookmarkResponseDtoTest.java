package com.itachallenge.user.dto.userinteraction.bookmark;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkResponseDtoTest {

    private BookmarkResponseDto bookmarkResponseDto;
    private final UUID testUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private final UUID testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
    private final UUID testChallengeId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);

    @BeforeEach
    void setUp() {
        bookmarkResponseDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
    }

    @Test
    void testNoArgsConstructor() {
        BookmarkResponseDto dto = new BookmarkResponseDto();
        assertNotNull(dto);
        assertNull(dto.getUuid());
        assertNull(dto.getUserId());
        assertNull(dto.getChallengeId());
        assertNull(dto.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        BookmarkResponseDto dto = new BookmarkResponseDto(testUuid, testUserId, testChallengeId, testDateTime);
        
        assertEquals(testUuid, dto.getUuid());
        assertEquals(testUserId, dto.getUserId());
        assertEquals(testChallengeId, dto.getChallengeId());
        assertEquals(testDateTime, dto.getCreatedAt());
    }

    @Test
    void testGettersAndSetters() {
        UUID newUuid = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        UUID newChallengeId = UUID.randomUUID();
        LocalDateTime newDateTime = LocalDateTime.now();

        bookmarkResponseDto.setUuid(newUuid);
        bookmarkResponseDto.setUserId(newUserId);
        bookmarkResponseDto.setChallengeId(newChallengeId);
        bookmarkResponseDto.setCreatedAt(newDateTime);

        assertEquals(newUuid, bookmarkResponseDto.getUuid());
        assertEquals(newUserId, bookmarkResponseDto.getUserId());
        assertEquals(newChallengeId, bookmarkResponseDto.getChallengeId());
        assertEquals(newDateTime, bookmarkResponseDto.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        BookmarkResponseDto sameDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();

        BookmarkResponseDto differentDto = BookmarkResponseDto.builder()
                .uuid(UUID.randomUUID())
                .userId(testUserId)
                .challengeId(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .build();

        assertEquals(bookmarkResponseDto, sameDto);
        assertNotEquals(bookmarkResponseDto, differentDto);
        assertNotEquals(null, bookmarkResponseDto);
        assertNotEquals(new Object(), bookmarkResponseDto);

        assertEquals(bookmarkResponseDto.hashCode(), sameDto.hashCode());
        assertNotEquals(bookmarkResponseDto.hashCode(), differentDto.hashCode());
    }

    @Test
    void testToString() {
        String dtoString = bookmarkResponseDto.toString();
        assertTrue(dtoString.contains("uuid=" + testUuid));
        assertTrue(dtoString.contains("userId=" + testUserId));
        assertTrue(dtoString.contains("challengeId=" + testChallengeId));
        assertTrue(dtoString.contains("createdAt=" + testDateTime));
    }

    @Test
    void testJsonProperties() {
        assertEquals(testUuid, bookmarkResponseDto.getUuid());
        assertEquals(testUserId, bookmarkResponseDto.getUserId());
        assertEquals(testChallengeId, bookmarkResponseDto.getChallengeId());
        assertEquals(testDateTime, bookmarkResponseDto.getCreatedAt());
    }

    @Test
    void testBuilder() {
        assertNotNull(BookmarkResponseDto.builder());
        
        BookmarkResponseDto builtDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();

        assertNotNull(builtDto);
        assertEquals(testUuid, builtDto.getUuid());
        assertEquals(testUserId, builtDto.getUserId());
        assertEquals(testChallengeId, builtDto.getChallengeId());
        assertEquals(testDateTime, builtDto.getCreatedAt());
    }
}
