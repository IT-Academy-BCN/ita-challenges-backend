package com.itachallenge.challenge.dto;




import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ChallengeFilterDtoTest {

    @Test
    public void testConstructorAndGetters() {
        String languageId = "1234";
        String level = "MEDIUM";
        UUID tag1 = UUID.randomUUID();
        UUID tag2 = UUID.randomUUID();
        List<UUID> tags = List.of(tag1, tag2);
        int offset = 5;
        int limit = 10;

        ChallengeFilterDto dto = new ChallengeFilterDto(languageId, level, tags, offset, limit);

        assertEquals(languageId, dto.getIdLanguage());
        assertEquals(level, dto.getLevel());
        assertEquals(tags, dto.getTags());
        assertEquals((Integer)offset, dto.getOffset());
        assertEquals((Integer)limit, dto.getLimit());
    }

    @Test
    public void testSetters() {
        ChallengeFilterDto dto = new ChallengeFilterDto(null, null, null, 0, -1);

        String newLanguageId = "5678";
        String newLevel = "EASY";
        List<UUID> newTags = List.of(UUID.randomUUID());
        int newOffset = 2;
        int newLimit = 20;

        dto.setIdLanguage(newLanguageId);
        dto.setLevel(newLevel);
        dto.setTags(newTags);
        dto.setOffset(newOffset);
        dto.setLimit(newLimit);

        assertEquals(newLanguageId, dto.getIdLanguage());
        assertEquals(newLevel, dto.getLevel());
        assertEquals(newTags, dto.getTags());
        assertEquals((Integer)newOffset, dto.getOffset());
        assertEquals((Integer)newLimit, dto.getLimit());
    }

    @Test
    public void testDefaultValues() {
        ChallengeFilterDto dto = new ChallengeFilterDto(null, null, null, null, null);


        assertNull(dto.getIdLanguage());
        assertNull(dto.getLevel());
        assertNull(dto.getTags());
        assertNull(dto.getOffset());
        assertNull(dto.getLimit());
    }
}

