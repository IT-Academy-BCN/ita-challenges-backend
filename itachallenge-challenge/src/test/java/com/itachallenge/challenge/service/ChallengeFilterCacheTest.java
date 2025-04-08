package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@EnableCaching
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChallengeFilterCacheTest {

    @Autowired
    private ChallengeServiceImpl challengeService;

    @MockBean
    private ChallengeRepository challengeRepository;

    @MockBean
    private LanguageService languageService;

    @MockBean
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        ChallengeDocument challengeDoc = new ChallengeDocument();
        ChallengeDto challengeDto = new ChallengeDto();

        when(challengeRepository.findAllByUuidNotNullExcludingTestingValues())
                .thenReturn(Flux.just(challengeDoc));
        when(languageService.filterByLanguage(any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(tagService.filterByTags(any(), any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

    }

    @Test
    void testGetChallengesByFilter_cacheWorks() {
        Optional<String> idLanguage = Optional.of("java");
        Optional<String> level = Optional.of("easy");
        Optional<List<UUID>> tags = Optional.empty();
        int offset = 0;
        int limit = 10;


        challengeService.getChallengesByFilter(idLanguage, level, offset, limit, tags).block();


        challengeService.getChallengesByFilter(idLanguage, level, offset, limit, tags).block();


        verify(challengeRepository, times(1)).findAllByUuidNotNullExcludingTestingValues();
    }
}



