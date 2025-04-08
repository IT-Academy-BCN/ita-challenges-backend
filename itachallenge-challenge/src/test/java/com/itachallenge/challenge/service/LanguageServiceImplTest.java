package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.LanguageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    void shouldReturnLanguageDocumentWhenIdExists() {
        UUID id = UUID.randomUUID();
        LanguageDocument expectedDocument = new LanguageDocument();
        expectedDocument.setIdLanguage(id);
        expectedDocument.setLanguageName("English");

        Mockito.when(languageRepository.findByIdLanguage(id)).thenReturn(Mono.just(expectedDocument));

        StepVerifier.create(languageService.findByIdLanguage(id))
                .expectNextMatches(doc -> doc.getIdLanguage().equals(id) && doc.getLanguageName().equals("English"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {
        UUID id = UUID.randomUUID();

        Mockito.when(languageRepository.findByIdLanguage(id)).thenReturn(Mono.empty());

        StepVerifier.create(languageService.findByIdLanguage(id))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorIfRepositoryFails() {
        UUID id = UUID.randomUUID();
        RuntimeException exception = new RuntimeException("Database error");

        Mockito.when(languageRepository.findByIdLanguage(id)).thenReturn(Mono.error(exception));

        StepVerifier.create(languageService.findByIdLanguage(id))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    void shouldReturnLanguageDocumentWhenLanguageNameExists() {
        String languageName = "Java";
        LanguageDocument expected = new LanguageDocument();
        expected.setIdLanguage(UUID.randomUUID());
        expected.setLanguageName(languageName);

        Mockito.when(languageRepository.findFirstByLanguageName(languageName)).thenReturn(Mono.just(expected));

        StepVerifier.create(languageService.findFirstByLanguageName(languageName))
                .expectNextMatches(doc -> doc.getLanguageName().equals(languageName))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenLanguageNameDoesNotExist() {
        String languageName = "NonExistentLanguage";

        Mockito.when(languageRepository.findFirstByLanguageName(languageName)).thenReturn(Mono.empty());

        StepVerifier.create(languageService.findFirstByLanguageName(languageName))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void shouldPropagateErrorWhenRepositoryFails() {
        String languageName = "Python";
        RuntimeException exception = new RuntimeException("Database error");

        Mockito.when(languageRepository.findFirstByLanguageName(languageName)).thenReturn(Mono.error(exception));

        StepVerifier.create(languageService.findFirstByLanguageName(languageName))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database error"))
                .verify();
    }

//    @DisplayName("Cache - getAllLanguages")
//    @Test
//    void getAllLanguages_cacheTest() {
//        // Arrange
//        LanguageDocument languageDocument1 = new LanguageDocument(UUID.randomUUID(), "Javascript", "https://image-default.com/javascript.png");
//        when(languageRepository.findAll()).thenReturn(Flux.just(languageDocument1));
//
//        // Primera llamada
//        GenericResultDto<LanguageDto> result1 = languageService.getAllLanguages().block();
//        assertNotNull(result1);
//        assertEquals(1, result1.getResults().length);
//
//        // Segunda llamada usando cache
//        GenericResultDto<LanguageDto> result2 = languageService.getAllLanguages().block();
//        assertNotNull(result2);
//        assertEquals(1, result2.getResults().length);
//
//
//        verify(languageRepository, times(1)).findAll();
//    }
}

