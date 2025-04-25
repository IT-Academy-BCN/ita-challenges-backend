package com.itachallenge.challenge.service;


import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.exception.TagNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.TagRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private DocumentToDtoConverter<TagDocument, TagDto> tagConverter = new DocumentToDtoConverter<>();

    @InjectMocks
    private TagServiceImpl tagService;

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
    @DisplayName("convertir UUIDs en TagDocuments")
    void testConvertIdTagFromTag_Document_Success() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        TagDocument tag1 = new TagDocument(id1, "POO", "Programación orientada a objetos");
        TagDocument tag2 = new TagDocument(id2, "Lógica", "Retos de lógica");

        when(tagRepository.findById(id1)).thenReturn(Mono.just(tag1));
        when(tagRepository.findById(id2)).thenReturn(Mono.just(tag2));

        List<UUID> tagIds = List.of(id1, id2);

        Set<TagDocument> result = tagService.convertIdTagFromTagDocument(tagIds);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(tag -> tag.getIdTag().equals(id1)));
        assertTrue(result.stream().anyMatch(tag -> tag.getIdTag().equals(id2)));

        verify(tagRepository).findById(id1);
        verify(tagRepository).findById(id2);
    }


    @Test
    @DisplayName("lanzar excepción cuando no se encuentra el TagDocument por UUID")
    void testConvertIdTagFromTag_TagDocumentNotFound() {
        UUID missingId = UUID.randomUUID();
        when(tagRepository.findById(missingId)).thenReturn(Mono.empty());

        List<UUID> tagsAssigned = List.of(missingId);

        TagNotFoundException exception = assertThrows(
                TagNotFoundException.class,
                () -> tagService.convertIdTagFromTagDocument(tagsAssigned)
        );

        assertEquals("Tag not found: " + missingId, exception.getMessage());
        verify(tagRepository).findById(missingId);
    }

    @Test
    @DisplayName("Get exception when tag is not found")
    void getValidatedTags_notFound_test(){
        UUID missingId = UUID.randomUUID();
        when(tagRepository.findById(missingId)).thenReturn(Mono.empty());
        List<UUID> tagsAssigned = List.of(missingId);

        StepVerifier.create(tagService.getValidatedTags(tagsAssigned))
                .expectError(TagNotFoundException.class)
                .verify();
        verify(tagRepository).findById(missingId);
    }

    @Test
    @DisplayName("Returns Mono<true> when tag has been found")
    void getValidatedTags_returnsTrue_test(){
        TagDocument tagDocument = new TagDocument(UUID.randomUUID(), "Tag Title", "Tag Description");
        when(tagRepository.findById(tagDocument.getIdTag())).thenReturn(Mono.just(tagDocument));
        List<UUID> tagsAssigned = List.of(tagDocument.getIdTag());
        StepVerifier.create(tagService.getValidatedTags(tagsAssigned))
                .expectNext(true).verifyComplete();
        verify(tagRepository).findById(tagDocument.getIdTag());
    }
}


