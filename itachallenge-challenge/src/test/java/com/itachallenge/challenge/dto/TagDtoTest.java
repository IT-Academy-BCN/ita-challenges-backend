package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TagDtoTest {

    @Test
    void testTagDtoAllArgsConstructorAndGetters() {
        UUID id = UUID.randomUUID();
        String name = "POO";
        String description = "Programación orientada a objetos";

        TagDto tagDto = new TagDto(id, name, description,UUID.randomUUID());

        assertEquals(id, tagDto.getTagId());
        assertEquals(name, tagDto.getTagName());
        assertEquals(description, tagDto.getTagDescription());
        assertNotNull(tagDto.getLanguageId());
    }

    @Test
    void testTagDtoNoArgsConstructorAndSetters() {
        UUID id = UUID.randomUUID();
        String name = "Algoritmos";
        String description = "Retos de lógica";

        TagDto tagDto = new TagDto();
        tagDto.setTagId(id);
        tagDto.setTagName(name);
        tagDto.setTagDescription(description);

        assertEquals(id, tagDto.getTagId());
        assertEquals(name, tagDto.getTagName());
        assertEquals(description, tagDto.getTagDescription());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TagDto tagDto = new TagDto(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                "Recursividad",
                "Funciones que se llaman a sí mismas",UUID.randomUUID());

        String json = mapper.writeValueAsString(tagDto);

        assertTrue(json.contains("\"tag_name\":\"Recursividad\""));
        assertTrue(json.contains("\"tag_description\":\"Funciones que se llaman a sí mismas\""));
        assertTrue(json.contains("\"id_tag\":\"123e4567-e89b-12d3-a456-426614174000\""));
    }
}

