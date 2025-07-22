package com.itachallenge.challenge.dto;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource(properties = { // <-- New Annotation
        "token.signing.key=c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0",
        "token.expiration.minutes=600"
})
class SolvedDtoTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        SolvedDto dto = new SolvedDto();
        dto.setSolved(true);
        dto.setTimesSolved(5);

        assertTrue(dto.isSolved());
        assertEquals(5, dto.getTimesSolved());
    }

    @Test
    void testAllArgsConstructor() {
        SolvedDto dto = new SolvedDto(false, 3);

        assertFalse(dto.isSolved());
        assertEquals(3, dto.getTimesSolved());
    }

    @Test
    void testEqualsAndHashCode() {
        SolvedDto dto1 = new SolvedDto(true, 2);
        SolvedDto dto2 = new SolvedDto(true, 2);
        SolvedDto dto3 = new SolvedDto(false, 5);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        SolvedDto dto = new SolvedDto(true, 7);
        String result = dto.toString();

        assertTrue(result.contains("isSolved=true"));
        assertTrue(result.contains("timesSolved=7"));
    }
}
