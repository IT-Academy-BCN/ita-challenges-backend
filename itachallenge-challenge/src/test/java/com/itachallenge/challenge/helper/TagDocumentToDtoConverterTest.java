package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.TagDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TagDocumentToDtoConverterTest {

    private DocumentToDtoConverter<TagDocument, TagDto> mapper;

    private TagDocument tagDocument1;

    private TagDocument tagDocument2;

    private TagDto tagDto1;

    private TagDto tagDto2;


    @BeforeEach
    public void setUp() {
        mapper  = new DocumentToDtoConverter();

        UUID[] tagsID = new UUID[]{UUID.randomUUID(), UUID.randomUUID()};
        String[] tagsNames = new String[]{"POO", "Refactorizacion"};
        String description = "bla bla bla";

        tagDocument1 = new TagDocument(tagsID[0], tagsNames[0], description, UUID.randomUUID());
        tagDocument2 = new TagDocument(tagsID[1], tagsNames[1], description, UUID.randomUUID());

        tagDto1 = new TagDto(tagsID[0], tagsNames[0], description, UUID.randomUUID());
        tagDto2 = new TagDto(tagsID[1], tagsNames[1], description, UUID.randomUUID());

    }

    @Test
    @DisplayName("Conversion from document to dto when the field types and names perfectly match the source")
    void testConvertTagDocumentToTagDto() {

        TagDocument tagDocumentMocked = tagDocument1;
        TagDto resultDto = mapper.convertDocumentToDto(tagDocumentMocked, TagDto.class);
        TagDto expectedDto = tagDto1;


        assertEquals(expectedDto.getTagId(), resultDto.getTagId());
        assertEquals(expectedDto.getTagName(), resultDto.getTagName());
        assertEquals(expectedDto.getTagDescription(), resultDto.getTagDescription());
    }

    @Test
    @DisplayName("Test convertFluxEntityToFluxDto method")
    void testConvertFluxEntityToFluxDto() {
        Flux<TagDocument> documentFlux = Flux.just(tagDocument1, tagDocument2);
        Flux<TagDto> resultFlux = mapper.convertDocumentFluxToDtoFlux(documentFlux, TagDto.class);
        Flux<TagDto> expectedFlux = Flux.just(tagDto1, tagDto2);

        StepVerifier.create(resultFlux)
                .assertNext(tagDto -> {
                    Assertions.assertEquals(tagDto1.getTagId(), tagDto.getTagId());
                    Assertions.assertEquals(tagDto1.getTagName(), tagDto.getTagName());
                    Assertions.assertEquals(tagDto1.getTagDescription(), tagDto.getTagDescription());
                })
                .assertNext(tagDto -> {
                    Assertions.assertEquals(tagDto2.getTagId(), tagDto.getTagId());
                    Assertions.assertEquals(tagDto2.getTagName(), tagDto.getTagName());
                    Assertions.assertEquals(tagDto2.getTagDescription(), tagDto.getTagDescription());
                })
                .expectComplete()
                .verify();

        assertThat(expectedFlux.blockFirst()).usingRecursiveComparison().isEqualTo(resultFlux.blockFirst());
        assertThat(expectedFlux.blockLast()).usingRecursiveComparison().isEqualTo(resultFlux.blockLast());
    }

}

