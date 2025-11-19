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

        assertEquals(bookmarkResponseDto, bookmarkResponseDto);
        
        assertEquals(bookmarkResponseDto, sameDto);
        assertEquals(sameDto, bookmarkResponseDto);
        
        BookmarkResponseDto anotherSameDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
        assertEquals(sameDto, anotherSameDto);
        assertEquals(bookmarkResponseDto, anotherSameDto);

        assertEquals(bookmarkResponseDto.hashCode(), sameDto.hashCode());
        assertNotEquals(bookmarkResponseDto.hashCode(), differentDto.hashCode());
        
        int initialHashCode = bookmarkResponseDto.hashCode();
        assertEquals(initialHashCode, bookmarkResponseDto.hashCode());
    }
    
    @Test
    void testEquals_WithNullFields() {
        BookmarkResponseDto dtoWithNulls = new BookmarkResponseDto();
        BookmarkResponseDto anotherDtoWithNulls = new BookmarkResponseDto();
        
        assertEquals(dtoWithNulls, anotherDtoWithNulls);
        assertEquals(dtoWithNulls.hashCode(), anotherDtoWithNulls.hashCode());
    }
    
    @Test
    void testEquals_WithDifferentFieldValues() {
        BookmarkResponseDto differentUuid = BookmarkResponseDto.builder()
                .uuid(UUID.randomUUID())
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
                
        BookmarkResponseDto differentUserId = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(UUID.randomUUID())
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
                
        BookmarkResponseDto differentChallengeId = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(UUID.randomUUID())
                .createdAt(testDateTime)
                .build();
                
        BookmarkResponseDto differentCreatedAt = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(LocalDateTime.now())
                .build();
        
        assertNotEquals(bookmarkResponseDto, differentUuid);
        assertNotEquals(bookmarkResponseDto, differentUserId);
        assertNotEquals(bookmarkResponseDto, differentChallengeId);
        assertNotEquals(bookmarkResponseDto, differentCreatedAt);
        
        assertNotEquals(bookmarkResponseDto.hashCode(), differentUuid.hashCode());
        assertNotEquals(bookmarkResponseDto.hashCode(), differentUserId.hashCode());
        assertNotEquals(bookmarkResponseDto.hashCode(), differentChallengeId.hashCode());
        assertNotEquals(bookmarkResponseDto.hashCode(), differentCreatedAt.hashCode());
    }
    
    @Test
    void testHashCodeConsistency() {
        int initialHashCode = bookmarkResponseDto.hashCode();
        
        assertEquals(initialHashCode, bookmarkResponseDto.hashCode());
        assertEquals(initialHashCode, bookmarkResponseDto.hashCode());
        
        BookmarkResponseDto sameDto = BookmarkResponseDto.builder()
                .uuid(testUuid)
                .userId(testUserId)
                .challengeId(testChallengeId)
                .createdAt(testDateTime)
                .build();
                
        assertEquals(bookmarkResponseDto.hashCode(), sameDto.hashCode());
    }
    
    @Test
    void testEqualsAndHashCode_WithNullFields() {
        BookmarkResponseDto dto1 = new BookmarkResponseDto(null, null, null, null);
        BookmarkResponseDto dto2 = new BookmarkResponseDto(null, null, null, null);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        
        BookmarkResponseDto dto3 = new BookmarkResponseDto(testUuid, null, null, null);
        BookmarkResponseDto dto4 = new BookmarkResponseDto(testUuid, null, null, null);
        
        assertEquals(dto3, dto4);
        assertEquals(dto3.hashCode(), dto4.hashCode());
    }

    @Test
    void testToString() {
        String dtoString = bookmarkResponseDto.toString();
        assertTrue(dtoString.contains("uuid=" + testUuid));
        assertTrue(dtoString.contains("userId=" + testUserId));
        assertTrue(dtoString.contains("challengeId=" + testChallengeId));
        assertTrue(dtoString.contains("createdAt=" + testDateTime));
        
        BookmarkResponseDto nullDto = new BookmarkResponseDto();
        String nullDtoString = nullDto.toString();
        assertTrue(nullDtoString.contains("uuid=null"));
        assertTrue(nullDtoString.contains("userId=null"));
        assertTrue(nullDtoString.contains("challengeId=null"));
        assertTrue(nullDtoString.contains("createdAt=null"));
    }

    @Test
    void testJsonProperties() {
        assertEquals(testUuid, bookmarkResponseDto.getUuid());
        assertEquals(testUserId, bookmarkResponseDto.getUserId());
        assertEquals(testChallengeId, bookmarkResponseDto.getChallengeId());
        assertEquals(testDateTime, bookmarkResponseDto.getCreatedAt());
        
        try {
            java.lang.reflect.Field uuidField = BookmarkResponseDto.class.getDeclaredField("uuid");
            com.fasterxml.jackson.annotation.JsonProperty uuidAnnotation = uuidField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("uuid_favorite", uuidAnnotation.value());
            
            java.lang.reflect.Field userIdField = BookmarkResponseDto.class.getDeclaredField("userId");
            com.fasterxml.jackson.annotation.JsonProperty userIdAnnotation = userIdField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("user_id", userIdAnnotation.value());
            
            java.lang.reflect.Field challengeIdField = BookmarkResponseDto.class.getDeclaredField("challengeId");
            com.fasterxml.jackson.annotation.JsonProperty challengeIdAnnotation = challengeIdField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("challenge_id", challengeIdAnnotation.value());
            
            java.lang.reflect.Field createdAtField = BookmarkResponseDto.class.getDeclaredField("createdAt");
            com.fasterxml.jackson.annotation.JsonProperty createdAtAnnotation = createdAtField.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
            assertEquals("created_at", createdAtAnnotation.value());
        } catch (NoSuchFieldException e) {
            fail("Field not found: " + e.getMessage());
        }
    }

    @Test
    void testBuilder() {
        BookmarkResponseDto.BookmarkResponseDtoBuilder builder = BookmarkResponseDto.builder();
        assertNotNull(builder);
        
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
        
        BookmarkResponseDto nullDto = BookmarkResponseDto.builder().build();
        assertNull(nullDto.getUuid());
        assertNull(nullDto.getUserId());
        assertNull(nullDto.getChallengeId());
        assertNull(nullDto.getCreatedAt());
        
        assertNotNull(builder.toString());
    }
    
    @Test
    void testLombokAnnotations() {
        assertNotNull(bookmarkResponseDto.toString());
        assertTrue(bookmarkResponseDto.toString().contains(BookmarkResponseDto.class.getSimpleName()));
        
        assertNotNull(BookmarkResponseDto.builder());
        
        assertNotNull(new BookmarkResponseDto());
        assertNotNull(new BookmarkResponseDto(testUuid, testUserId, testChallengeId, testDateTime));
    }
}
