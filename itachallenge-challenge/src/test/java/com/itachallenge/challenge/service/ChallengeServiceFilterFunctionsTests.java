package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.TagDocument;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceFilterFunctionsTests {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private LanguageServiceImp languageService;

    private TagService tagService;
    private ChallengeServiceImp challengeService;

    private final String mockLanguageId = "123e4567-e89b-12d3-a456-426614174000";

    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");
    UUID uuid_3 = UUID.fromString("2f948de0-6f0c-4089-90b9-7f70a0812319");

    private ChallengeDocument challenge1;
    private ChallengeDocument challenge2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        languageService = new LanguageServiceImp();
        tagService = new TagService(); // si no depende de nada
        challengeService = new ChallengeServiceImp(); // idem

        UUID uuidLang1 = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuidLang2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");

        LanguageDocument language1 = new LanguageDocument(uuidLang1, "name1", "https://image-default.com/default.png");
        LanguageDocument language2 = new LanguageDocument(uuidLang2, "name2", "https://image-default.com/default.png");
        Set<LanguageDocument> languageSet = Set.of(language1, language2);
        Set<LanguageDocument> languageSet3 = Set.of(language1);

        DetailDocument detail = new DetailDocument("Description");

        TagDocument tag1 = new TagDocument(UUID.randomUUID(), "recursion", "Challenges about recursion");

        List<UUID> tags = new ArrayList<>(Arrays.asList(tag1.getIdTag()));
        List<UUID> solutionList = List.of(UUID.randomUUID(), UUID.randomUUID());

        challenge1 = new ChallengeDocument(uuid_2, "Challenge 2", "EASY", LocalDateTime.now(), detail,
                languageSet, solutionList, Topic.LISTS, 10, tags);
        challenge2 = new ChallengeDocument(uuid_3, "Challenge 3", "HARD", LocalDateTime.now(), detail,
                languageSet3, solutionList, Topic.COMPONENTS, 15, tags);
    }


    @Test
    public void testFilterByLanguage_present() {
        UUID uuid = UUID.fromString(mockLanguageId);
        LanguageDocument language = new LanguageDocument();
        language.setIdLanguage(uuid);

        // Challenge con language correcto
        challenge1.setLanguages(Set.of(language));
        // Challenge sin idiomas o con otros idiomas
        challenge2.setLanguages(Set.of());

        Flux<ChallengeDocument> inputChallenges = Flux.just(challenge1, challenge2);

        StepVerifier.create(languageService.filterByLanguage(inputChallenges, Optional.of(mockLanguageId)))
                .expectNext(challenge1)
                .verifyComplete();
    }


    @Test
    public void testFilterByLanguage_notPresent_returnsAll() {
        Flux<ChallengeDocument> allChallenges = Flux.just(challenge1, challenge2);

        StepVerifier.create(languageService.filterByLanguage(allChallenges, Optional.empty()))
                .expectNext(challenge1, challenge2)
                .verifyComplete();
    }


    @Test
    public void testFilterByLevel_present_filtersCorrectly() {
        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);

        StepVerifier.create(challengeService.filterByLevel(source, Optional.of("easy")))
                .expectNext(challenge1)
                .verifyComplete();
    }

    @Test
    public void testFilterByLevel_notPresent_returnsAll() {
        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);

        StepVerifier.create(challengeService.filterByLevel(source, Optional.empty()))
                .expectNext(challenge1, challenge2)
                .verifyComplete();
    }

    @Test
    public void testFilterByTags_present_matchesSome() {
        UUID tag1 = UUID.randomUUID();
        UUID tag2 = UUID.randomUUID();
        List<UUID> tags = List.of(tag1, tag2);
        TagDocument tagDoc1 = new TagDocument(tag1, "POO","bla bla");
        TagDocument tagDoc2 = new TagDocument(tag2, "hjs", "bla bla");


        challenge1.setTags(tagDoc1.getIdTag());

        challenge2.setTags(tagDoc2.getIdTag());

        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);

        StepVerifier.create(tagService.filterByTags(source, Optional.of(tags)))
                .expectNext(challenge1, challenge2)
                .verifyComplete();
    }


    @Test
    public void testFilterByTags_present_matchesNone() {
        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);
        List<UUID> tags = List.of(UUID.randomUUID());

        StepVerifier.create(tagService.filterByTags(source, Optional.of(tags)))
                .verifyComplete();
    }

    @Test
    public void testFilterByTags_emptyTags_returnsAll() {
        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);

        StepVerifier.create(tagService.filterByTags(source, Optional.of(List.of())))
                .expectNext(challenge1, challenge2)
                .verifyComplete();
    }

    @Test
    public void testFilterByTags_notPresent_returnsAll() {
        Flux<ChallengeDocument> source = Flux.just(challenge1, challenge2);

        StepVerifier.create(tagService.filterByTags(source, Optional.empty()))
                .expectNext(challenge1, challenge2)
                .verifyComplete();
    }
}

