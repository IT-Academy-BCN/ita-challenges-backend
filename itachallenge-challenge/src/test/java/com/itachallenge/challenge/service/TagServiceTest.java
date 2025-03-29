package com.itachallenge.challenge.service;


import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.exception.TagNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private DocumentToDtoConverter<TagDocument, TagDto> tagConverter = new DocumentToDtoConverter<>();

    @InjectMocks
    private TagService tagService;

    @Test
    @DisplayName("return todos los tags y generar GenericResultDto<TagDto>")
    void testGetAllTags() {

        TagDocument tag1 = new TagDocument(UUID.randomUUID(), "POO", "Programación orientada a objetos");
        TagDocument tag2 = new TagDocument(UUID.randomUUID(), "Algoritmos", "Retos de lógica y eficiencia");

        TagDto dto1 = new TagDto(tag1.getIdTag(), tag1.getTagName(), tag1.getTagDescription());
        TagDto dto2 = new TagDto(tag2.getIdTag(), tag2.getTagName(), tag2.getTagDescription());

        Flux<TagDocument> tagDocumentFlux = Flux.just(tag1, tag2);
        Flux<TagDto> tagDtoFlux = Flux.just(dto1, dto2);

        when(tagRepository.findAll()).thenReturn(tagDocumentFlux);
        when(tagConverter.convertDocumentFluxToDtoFlux(tagDocumentFlux, TagDto.class)).thenReturn(tagDtoFlux);


        Mono<GenericResultDto<TagDto>> resultMono = tagService.getAllTags();

        StepVerifier.create(resultMono)
                .assertNext(result -> {
                    assertNotNull(result);
                    assertEquals(2, result.getResults().length);
                    assertEquals("POO", result.getResults()[0].getTagName());
                    assertEquals("Algoritmos", result.getResults()[1].getTagName());
                    assertEquals(0, result.getOffset());
                    assertEquals(2, result.getLimit());
                })
                .verifyComplete();


        verify(tagRepository).findAll();
        verify(tagConverter).convertDocumentFluxToDtoFlux(tagDocumentFlux, TagDto.class);
    }

    @Test
    @DisplayName("convertir Strings en TagDocuments")
    void testConvertStringNameToTag_Success() {

        String name1 = "POO";
        String name2 = "Algoritmos";

        TagDocument tag1 = new TagDocument(UUID.randomUUID(), name1, "Programación orientada a objetos");
        TagDocument tag2 = new TagDocument(UUID.randomUUID(), name2, "Retos de lógica");

        when(tagRepository.findByTagName(name1)).thenReturn(Mono.just(tag1));
        when(tagRepository.findByTagName(name2)).thenReturn(Mono.just(tag2));

        List<String> tagsInstead = List.of(name1, name2);


        List<TagDocument> result = tagService.convertStringNameToTag(tagsInstead);


        assertEquals(2, result.size());
        assertEquals(name1, result.get(0).getTagName());
        assertEquals(name2, result.get(1).getTagName());

        verify(tagRepository).findByTagName(name1);
        verify(tagRepository).findByTagName(name2);
    }

    @Test
    @DisplayName("saltar la excepcion")
    void testConvertStringNameToTag_TagNotFound() {

        String missingTag = "Inexistente";
        when(tagRepository.findByTagName(missingTag)).thenReturn(Mono.empty());

        List<String> tagsAssigned = List.of(missingTag);

        TagNotFoundException exception = assertThrows(
                TagNotFoundException.class,
                () -> tagService.convertStringNameToTag(tagsAssigned)
        );

        assertEquals("Tag not found: " + missingTag, exception.getMessage());
        verify(tagRepository).findByTagName(missingTag);
    }
}


