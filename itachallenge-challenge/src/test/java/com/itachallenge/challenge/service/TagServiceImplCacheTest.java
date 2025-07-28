package com.itachallenge.challenge.service;

import com.itachallenge.challenge.config.CacheConfig;
import com.itachallenge.challenge.config.TestChallengeConfig;
import com.itachallenge.challenge.controller.ChallengeController;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.TagDto;
import com.itachallenge.challenge.repository.TagRepository;
import com.itachallenge.jwtcore.service.IJwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import({CacheConfig.class, TestChallengeConfig.class})

class TagServiceImplCacheTest {

    @MockBean
    private TagRepository tagRepository;

    @Autowired
    private ITagService tagService;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private ChallengeController challengeController;

    @MockBean
    private IJwtService jwtService;

    @BeforeEach
    void setup() {
        cacheManager.getCache("tagsByLanguage").clear();
    }


    @Test
    void testGetTagsByLanguageIdUsesCache() {
        UUID languageId = UUID.randomUUID();
        TagDocument tag = new TagDocument(UUID.randomUUID(), "Algoritmos", "bla bla", UUID.randomUUID());
        when(tagRepository.findByLanguageId(languageId)).thenReturn(Flux.just(tag));

        // Primera llamada
        GenericResultDto<TagDto> result1 = tagService.getTagsByLanguageId(languageId).block();
        assertNotNull(result1);
        assertEquals(1, result1.getResults().length);

        // Segunda llamada
        GenericResultDto<TagDto> result2 = tagService.getTagsByLanguageId(languageId).block();
        assertNotNull(result2);
        assertEquals(1, result2.getResults().length);


        verify(tagRepository, times(1)).findByLanguageId(languageId);
    }
}

